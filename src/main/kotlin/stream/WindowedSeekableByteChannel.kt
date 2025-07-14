package org.bread_experts_group.stream

import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel
import kotlin.math.min

class WindowedSeekableByteChannel(
	private val base: SeekableByteChannel,
	val start: Long,
	val end: Long
) : SeekableByteChannel {
	private var position = 0L
	override fun position(): Long = position

	@Synchronized
	override fun position(newPos: Long): SeekableByteChannel {
		require(newPos >= 0 && newPos <= size()) { "Position out of slice range" }
		position = newPos
		return this
	}

	override fun size(): Long = end - start

	@Synchronized
	override fun read(dst: ByteBuffer): Int = synchronized(base) {
		if (position >= size()) return -1
		val returnTo = base.position()
		base.position(start + position)
		val savedLimit = dst.limit()
		val writable = min(dst.remaining().toLong(), size() - position)
			.coerceAtMost(Int.MAX_VALUE.toLong())
			.toInt()
		dst.limit(dst.position() + writable)
		val read = base.read(dst)
		position += read
		dst.limit(savedLimit)
		base.position(returnTo)
		read
	}

	@Synchronized
	override fun write(src: ByteBuffer): Int {
		TODO("Write")
	}

	override fun truncate(size: Long): SeekableByteChannel = throw UnsupportedOperationException()
	override fun close() = base.close()
	override fun isOpen() = base.isOpen
}