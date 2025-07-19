package org.bread_experts_group.channel

import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel
import kotlin.math.min

class ByteBufferChannel(val buffer: ByteBuffer) : SeekableByteChannel {
	override fun read(dst: ByteBuffer): Int {
		if (!buffer.hasRemaining()) return -1
		val saved = buffer.limit()
		val length = min(buffer.remaining(), dst.remaining())
		buffer.limit(buffer.position() + length)
		dst.put(buffer)
		buffer.limit(saved)
		return length
	}

	override fun write(src: ByteBuffer?): Int = throw UnsupportedOperationException()
	override fun position(): Long = buffer.position().toLong()
	override fun position(newPosition: Long): SeekableByteChannel {
		buffer.position(newPosition.coerceAtMost(buffer.limit().toLong()).toInt())
		return this
	}

	override fun size(): Long = buffer.limit().toLong()
	override fun truncate(size: Long): SeekableByteChannel = throw UnsupportedOperationException()
	override fun isOpen(): Boolean = true
	override fun close() {}
}