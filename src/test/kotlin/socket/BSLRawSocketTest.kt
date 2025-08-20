package org.bread_experts_group.socket

import org.bread_experts_group.io.reader.ReadingByteBuffer
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.protocol.ip.InternetProtocol
import org.junit.jupiter.api.Test
import java.net.InetAddress
import java.nio.ByteBuffer

class BSLRawSocketTest {
	val logger = ColoredHandler.newLogger("TMP logger")

	@Test
	fun read() {
		val socket = BSLInternetRawSocket.open(BSLInternetProtocolSocketType.VERSION_4)
		socket.bind(BSLInetSocketAddress(InetAddress.getByName("127.0.01")))
		socket.promiscuous(true)
		val readable = ReadingByteBuffer(socket, ByteBuffer.allocate(65535), null)
		for (i in 1..5) {
			val decoded = InternetProtocol.layout.read(readable)
			logger.info(decoded.toString())
		}
		socket.close()
	}
}