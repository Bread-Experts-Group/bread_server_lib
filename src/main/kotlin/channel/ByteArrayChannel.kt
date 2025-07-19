package org.bread_experts_group.channel

import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel
import kotlin.math.min

class ByteArrayChannel(val array: ByteArray) : SeekableByteChannel {
	private var position = 0

	override fun read(dst: ByteBuffer): Int {
		if (position == array.size) return -1
		val length = min(array.size - position, dst.remaining())
		dst.put(array, position, length)
		position += length
		return length
	}

	override fun write(src: ByteBuffer?): Int = throw UnsupportedOperationException()
	override fun position(): Long = position.toLong()
	override fun position(newPosition: Long): SeekableByteChannel {
		position = newPosition.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
		return this
	}

	override fun size(): Long = array.size.toLong()
	override fun truncate(size: Long): SeekableByteChannel = throw UnsupportedOperationException()
	override fun isOpen(): Boolean = true
	override fun close() {}
}