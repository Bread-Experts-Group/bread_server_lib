package org.bread_experts_group.socket

import org.bread_experts_group.io.reader.ReadingByteBuffer
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

class BSLBluetoothSocketTest {
	val logger = ColoredHandler.newLogger("TMP logger")

	@Test
	fun read() {
		val socket = BSLBluetoothSocket.open()
		socket.bind(BSLBluetoothSocketAddress(byteArrayOf(0, 0, 0, 0, 0, 0)))
		val readable = ReadingByteBuffer(socket, ByteBuffer.allocate(65535), null)
		for (i in 1..5) {
			logger.info(socket.readDatagram(readable.buffer).toString())
		}
		socket.close()
	}
}