package org.bread_experts_group.http_router

import java.io.OutputStream
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class SocketChannelOutputStream(private val from: SocketChannel) : OutputStream() {
	private val buffer = ByteBuffer.allocate(from.getOption(StandardSocketOptions.SO_SNDBUF))
	override fun write(b: Int) {
		buffer.clear()
		buffer.put(b.toByte())
		buffer.flip()
		from.write(buffer)
	}
}