package org.bread_experts_group.socket

import org.bread_experts_group.channel.ReadingByteBuffer
import org.bread_experts_group.socket.protocol.InternetProtocolV4
import org.bread_experts_group.socket.protocol.InternetProtocolV4Transport
import org.junit.jupiter.api.Test
import java.net.InetAddress
import java.nio.ByteBuffer

class BSLRawSocketTest {
	@Test
	fun read() {
		val socket = BSLInternetRawSocket.open(BSLInternetProtocolSocketType.VERSION_4)
		socket.bind(BSLInetSocketAddress(InetAddress.getByName("127.0.0.1")))
		socket.promiscuous(true)
		val readable = ReadingByteBuffer(socket, ByteBuffer.allocate(65535), null)
		while (true) {
			val decoded = InternetProtocolV4.decode(readable)
			if (decoded.protocol.enum != InternetProtocolV4Transport.ICMP) continue
			println(decoded)
		}
		socket.close()
	}
}