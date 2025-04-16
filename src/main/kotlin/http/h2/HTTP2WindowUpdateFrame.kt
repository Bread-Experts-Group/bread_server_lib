package bread_experts_group.http.h2

import bread_experts_group.socket.read32
import java.io.InputStream

class HTTP2WindowUpdateFrame(
	identifier: Int,
	val windowSizeIncrement: Int
) : HTTP2Frame(HTTP2FrameType.WINDOW_UPDATE, identifier) {
	override fun toString(): String = super.toString() + " [$windowSizeIncrement]"

	companion object {
		fun read(stream: InputStream, length: Int): HTTP2WindowUpdateFrame {
			if (length != 4)
				throw HTTP2FrameSizeError("WINDOW_UPDATE size must be 4, got $length")
			stream.skip(1)
			return HTTP2WindowUpdateFrame(
				stream.read32(),
				stream.read32()
			)
		}
	}
}