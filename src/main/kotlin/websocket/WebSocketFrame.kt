package bread_experts_group.websocket

import bread_experts_group.SmartToString
import bread_experts_group.read16
import bread_experts_group.read32
import bread_experts_group.read64
import bread_experts_group.write16
import bread_experts_group.write64
import java.io.InputStream
import java.io.OutputStream
import kotlin.experimental.xor
import kotlin.random.Random

class WebSocketFrame(
	val opcode: WebSocketOpcode,
	val data: ByteArray,
	val mask: Boolean
) : SmartToString() {
	override fun gist(): String = "(WebSocket, $opcode) ${data.size}"

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
			val maskKey = Random.nextInt()
			data.forEachIndexed { index, b ->
				data[index] = b xor (maskKey shr (24 - ((index % 4) * 8))).toByte()
			}
		}
		stream.write(data)
	}

	companion object {
		fun read(stream: InputStream): WebSocketFrame {
			val opcode = WebSocketOpcode.mapping.getValue(stream.read() and 0b1111)
			val sizeMask = stream.read()
			val maskKey = if (sizeMask and 0b1_0000000 > 0) stream.read32() else null
			val size = sizeMask and 0b0_1111111
			val data = stream.readNBytes(
				if (size >= 127) stream.read64().also { if (it > Int.MAX_VALUE) error("Too high size $it") }.toInt()
				else if (size == 126) stream.read16()
				else size
			)
			if (maskKey != null) {
				data.forEachIndexed { index, b ->
					data[index] = b xor (maskKey shr (24 - ((index % 4) * 8))).toByte()
				}
			}
			return WebSocketFrame(opcode, data, maskKey != null)
		}
	}
}