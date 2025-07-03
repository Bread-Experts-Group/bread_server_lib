package org.bread_experts_group.http.html

import java.nio.ByteBuffer
import java.nio.channels.ClosedChannelException
import java.nio.channels.SeekableByteChannel
import kotlin.math.min

class VirtualFileChannel(private var from: ByteArray) : SeekableByteChannel {
	private var closed: Boolean = false
	private var position: Int = 0
	private fun ensureOpen() =
		if (closed) throw ClosedChannelException()
		else null

	override fun read(dst: ByteBuffer): Int {
		ensureOpen()
		if (position >= from.size) return -1
		val deposit = min(from.size - position, dst.remaining())
		dst.put(from, position, deposit)
		position += deposit
		return deposit
	}

	override fun write(src: ByteBuffer): Int {
		ensureOpen()
		val remainder = ByteArray(src.remaining())
		src.get(remainder)
		from += remainder
		position += remainder.size
		return remainder.size
	}

	override fun position(): Long = position.toLong()
	override fun position(newPosition: Long): SeekableByteChannel {
		ensureOpen()
		position = min(newPosition, Int.MAX_VALUE.toLong()).toInt()
		return this
	}

	override fun size(): Long = from.size.toLong()
	override fun truncate(size: Long): SeekableByteChannel {
		ensureOpen()
		if (size > from.size) return this
		from = from.sliceArray(0..<min(size, Int.MAX_VALUE.toLong()).toInt())
		return this
	}

	override fun isOpen(): Boolean = !closed
	override fun close() {
		closed = true
	}
}