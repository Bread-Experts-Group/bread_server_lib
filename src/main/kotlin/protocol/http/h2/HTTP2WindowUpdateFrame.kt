package org.bread_experts_group.protocol.http.h2

import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.write32
import java.io.InputStream
import java.io.OutputStream

class HTTP2WindowUpdateFrame(
	identifier: Int,
	val windowSizeIncrement: Int
) : HTTP2Frame(HTTP2FrameType.WINDOW_UPDATE, identifier) {
	override fun toString(): String = super.toString() + " [$windowSizeIncrement]"

	override fun collectFlags(): Int = 0
	override fun computeSize(): Long = 4L
	override fun write(stream: OutputStream) {
		super.write(stream)
		stream.write32(windowSizeIncrement)
	}

	companion object {
		fun read(stream: InputStream, length: Int, identifier: Int): HTTP2WindowUpdateFrame {
			if (length != 4)
				throw HTTP2FrameSizeError("WINDOW_UPDATE size must be 4, got $length")
			return HTTP2WindowUpdateFrame(
				identifier,
				stream.read32()
			)
		}
	}
}