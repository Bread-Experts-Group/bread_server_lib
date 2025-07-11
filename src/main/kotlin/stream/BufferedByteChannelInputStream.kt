package org.bread_experts_group.stream

import org.bread_experts_group.protocol.http.header.HTTPRangeHeader
import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel
import kotlin.math.min

class BufferedByteChannelInputStream(
	private val of: SeekableByteChannel,
	private val regions: List<Pair<Long, Long>> = listOf(0L to of.size() - 1)
) : LongInputStream() {
	constructor(of: SeekableByteChannel, range: HTTPRangeHeader) : this(of, range.ranges)

	private val buffer = ByteBuffer.allocate(4096)
	private var currentRegion: Int = 0
	private var length: Long = -1
	private var totalLength: ULong = regions.sumOf { (it.second - it.first).toULong() + 1uL }

	init {
		buffer.limit(0)
	}

	private fun refillBuffer() {
		buffer.rewind()
		if (length == -1L) {
			val (from, to) = regions[currentRegion]
			length = (to - from) + 1
			of.position(from)
			currentRegion++
		}
		buffer.limit(min(min(length, Int.MAX_VALUE.toLong()).toInt(), buffer.capacity()))
		val read = of.read(buffer)
		if (read == -1) throw FailQuickInputStream.EndOfStream()
		length -= read
		totalLength -= read.toULong()
		if (length == 0L && currentRegion < regions.lastIndex) length = -1
		buffer.flip()
	}

	override fun longAvailable(): ULong = totalLength
	override fun read(): Int = if (buffer.hasRemaining()) buffer.get().toInt()
	else if (length == 0L) -1
	else {
		refillBuffer()
		buffer.get().toInt()
	}

	override fun read(b: ByteArray, off: Int, len: Int): Int {
		if (!buffer.hasRemaining()) {
			if (length == 0L) return -1
			refillBuffer()
		}

		val maxLen = len.coerceAtMost(buffer.remaining())
		buffer.get(b, off, maxLen)
		return maxLen
	}
}