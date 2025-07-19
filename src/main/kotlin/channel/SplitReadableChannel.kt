package org.bread_experts_group.channel

import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel

open class SplitReadableChannel<T : ReadableByteChannel>(channels: Collection<T>) : ReadableByteChannel {
	val channels = ArrayDeque(channels)
	private var closed = false

	override fun read(dst: ByteBuffer): Int {
		if (channels.isEmpty()) return -1
		var acc = 0
		while (true) when (val len = channels.first().read(dst)) {
			-1 -> {
				channels.removeFirst()
				if (channels.isEmpty()) return acc
				continue
			}

			else -> {
				acc += len
				if (!dst.hasRemaining()) return acc
			}
		}
	}

	override fun isOpen(): Boolean = !closed
	override fun close() {
		if (closed) return
		closed = true
		for (c in channels) c.close()
	}
}