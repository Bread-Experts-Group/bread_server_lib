package org.bread_experts_group.http_router

import java.io.InputStream
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class SocketChannelInputStream(private val from: SocketChannel) : InputStream() {
	private val buffer = ByteBuffer.allocate(from.getOption(StandardSocketOptions.SO_RCVBUF))
	override fun read(): Int {
		if (buffer.hasRemaining()) return buffer.get().toInt()
		else {
			buffer.clear()
			from.read(buffer)
			buffer.flip()
			return buffer.get().toInt()
		}
	}
}