package org.bread_experts_group.channel

import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel

class WritingByteBuffer(
	val to: WritableByteChannel,
	val buffer: ByteBuffer
) : AutoCloseable {
	fun empty(amount: Int) {
		if (buffer.remaining() < amount) flush()
	}

	fun put(src: ByteArray) {
		empty(src.size)
		buffer.put(src)
	}

	fun transferFrom(from: ReadableByteChannel) {
		while (true) {
			empty(1)
			val read = from.read(buffer)
			if (read == -1) break
		}
	}

	fun flush() {
		buffer.flip()
		while (buffer.hasRemaining()) to.write(buffer)
		buffer.clear()
	}

	override fun close() = to.close()
}