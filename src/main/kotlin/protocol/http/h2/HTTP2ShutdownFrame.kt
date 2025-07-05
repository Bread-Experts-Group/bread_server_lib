package org.bread_experts_group.protocol.http.h2

import org.bread_experts_group.hex
import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.write32
import java.io.InputStream
import java.io.OutputStream

class HTTP2ShutdownFrame(
	identifier: Int,
	val lastStream: Int,
	val errorCode: HTTP2ErrorCode,
	val errorData: ByteArray
) : HTTP2Frame(HTTP2FrameType.SHUTDOWN, identifier) {
	override fun toString(): String = super.toString() + ", lastStream: ${hex(lastStream)}, " +
			"$errorCode, errorData: [${errorData.size}]\"${errorData.decodeToString()}\""

	override fun computeSize(): Long = 8L + errorData.size
	override fun collectFlags(): Int = 0
	override fun write(stream: OutputStream) {
		super.write(stream)
		stream.write32(lastStream)
		stream.write32(errorCode.code)
		stream.write(errorData)
	}

	companion object {
		fun read(stream: InputStream, length: Int, identifier: Int): HTTP2ShutdownFrame {
			if (identifier != 0) throw HTTP2ProtocolError("Shutdown frame had non-zero identifier [${hex(identifier)}]")
			return HTTP2ShutdownFrame(
				identifier,
				stream.read32(),
				HTTP2ErrorCode.mapping[stream.read32()] ?: HTTP2ErrorCode.INTERNAL_ERROR,
				stream.readNBytes(length - 8)
			)
		}
	}
}