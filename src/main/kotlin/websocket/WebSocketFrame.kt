package org.bread_experts_group.websocket

import org.bread_experts_group.socket.read16
import org.bread_experts_group.socket.read64
import org.bread_experts_group.socket.write16
import org.bread_experts_group.socket.write64
import java.io.InputStream
import java.io.OutputStream
import kotlin.experimental.xor
import kotlin.random.Random

class WebSocketFrame(
	val opcode: WebSocketOpcode,
	val data: ByteArray,
	val mask: Boolean
) {
	override fun toString(): String = "(WebSocket, $opcode) ${data.size}"

	fun write(stream: OutputStream) {
		stream.write(0b1_000_0000 or opcode.code)
		if (data.size > 65535) {
			stream.write(if (mask) 0b1_1111111 else 0b0_1111111)
			stream.write64(data.size.toLong())
		} else if (data.size > 125) {
			stream.write(if (mask) 0b1_0111111 else 0b0_0111111)
			stream.write16(data.size)
		} else stream.write((if (mask) 0b1_0000000 else 0) or data.size)
		if (mask) {
			val maskKey = Random.nextBytes(4)
			data.forEachIndexed { index, b ->
				data[index] = b xor maskKey[index % 4]
			}
		}
		stream.write(data)
	}

	companion object {
		fun read(stream: InputStream): WebSocketFrame {
			val opcode = WebSocketOpcode.mapping.getValue(stream.read() and 0b1111)
			val sizeMask = stream.read()
			val masked = sizeMask and 0b1_0000000 > 0
			val size = sizeMask and 0b0_1111111
			val trueSize =
				if (size >= 127) stream.read64().also { if (it > Int.MAX_VALUE) error("Too high size $it") }.toInt()
				else if (size == 126) stream.read16()
				else size
			if (masked) {
				val mask = listOf(
					stream.read(),
					stream.read(),
					stream.read(),
					stream.read()
				).map { it.toByte() }
				val data = stream.readNBytes(trueSize)
				data.forEachIndexed { index, b ->
					data[index] = b xor mask[index % 4]
				}
				return WebSocketFrame(opcode, data, true)
			}
			return WebSocketFrame(opcode, stream.readNBytes(trueSize), false)
		}
	}
}