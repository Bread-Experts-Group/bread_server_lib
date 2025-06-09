package org.bread_experts_group.http.h2

import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.write32
import java.io.InputStream
import java.io.OutputStream

class HTTP2StopStreamFrame(
	identifier: Int,
	val errorCode: HTTP2ErrorCode
) : HTTP2Frame(HTTP2FrameType.STOP_STREAM, identifier) {
	override fun toString(): String = super.toString() + ", $errorCode"

	override fun computeSize(): Long = 4L
	override fun collectFlags(): Int = 0
	override fun write(stream: OutputStream) {
		super.write(stream)
		stream.write32(errorCode.code)
	}

	companion object {
		fun read(stream: InputStream, length: Int, identifier: Int): HTTP2StopStreamFrame {
			if (length != 4)
				throw HTTP2FrameSizeError("STOP_STREAM size must be 4, got $length")
			return HTTP2StopStreamFrame(
				identifier,
				HTTP2ErrorCode.mapping[stream.read32()] ?: HTTP2ErrorCode.INTERNAL_ERROR,
			)
		}
	}
}