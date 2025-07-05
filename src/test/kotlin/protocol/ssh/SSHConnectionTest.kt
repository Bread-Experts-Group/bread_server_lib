package org.bread_experts_group.protocol.ssh

import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.net.InetSocketAddress
import java.net.ServerSocket

class SSHConnectionTest {
	val logger = ColoredHandler.newLogger("tmp")

	@Test
	fun test() = assertDoesNotThrow {
		val socket = ServerSocket()
		socket.bind(InetSocketAddress("0.0.0.0", 22))
		logger.info("Socket on ${socket.localSocketAddress}")
		val client = socket.accept()
		logger.info("Received ${client.remoteSocketAddress}")
		val connection = SSHConnection(client.inputStream, client.outputStream)
		while (true) {
			val next = connection.next().getOrThrow()
			logger.info(next.toString())
		}
	}
}