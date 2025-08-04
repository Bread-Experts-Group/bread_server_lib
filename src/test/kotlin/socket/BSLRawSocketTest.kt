package org.bread_experts_group.socket

import org.junit.jupiter.api.Test
import java.net.InetAddress
import java.nio.ByteBuffer

class BSLRawSocketTest {
	@Test
	fun read() {
		val socket = BSLInternetRawSocket.open(BSLInternetProtocolSocketType.VERSION_6)
		socket.bind(BSLInetSocketAddress(InetAddress.getByName("::1")))
		socket.promiscuous(true)
		val buffer = ByteBuffer.allocate(8192)
		while (true) {
			println(socket.localAddress)
			buffer.clear()
			val (address, read) = socket.readDatagram(buffer)
			val data = ByteArray(read)
			buffer.flip()
			buffer.get(data)
			println("ADDR: ${address.toHexString()}")
			println("DATA: ${data.toHexString()}")
		}
	}
}