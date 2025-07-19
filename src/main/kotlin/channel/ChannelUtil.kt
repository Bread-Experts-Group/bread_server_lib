package org.bread_experts_group.channel

import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel

fun ReadableByteChannel.skip(n: Long = Long.MAX_VALUE): Long {
	var skipped = 0L
	val buffer = ByteBuffer.allocateDirect(8192)
	while (true) {
		val read = this.read(buffer)
		if (read == -1) return skipped
		skipped += read
		buffer.clear()
	}
}