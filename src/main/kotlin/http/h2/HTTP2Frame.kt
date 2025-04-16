package bread_experts_group.http.h2

import bread_experts_group.hex
import bread_experts_group.read24ui
import java.io.InputStream

sealed class HTTP2Frame(
	val type: HTTP2FrameType,
	val identifier: Int
) {
	override fun toString(): String = "(HTTP/2, Frame) ${type.name}, ${hex(identifier)}"

	class HTTP2ProtocolError(reason: String) : Exception(reason)
	class HTTP2FrameSizeError(reason: String) : Exception(reason)
	class HTTP2FlowControlError(reason: String) : Exception(reason)

	companion object {
		fun read(stream: InputStream): HTTP2Frame {
			val length = stream.read24ui()
			val type = HTTP2FrameType.mapping[stream.read()] ?: HTTP2FrameType.OTHER
			return when (type) {
				HTTP2FrameType.DATA -> HTTP2DataFrame.read(stream, length)
				HTTP2FrameType.SETTINGS -> HTTP2SettingsFrame.read(stream, length)
				HTTP2FrameType.WINDOW_UPDATE -> HTTP2WindowUpdateFrame.read(stream, length)
				HTTP2FrameType.HEADERS -> HTTP2HeaderFrame.read(stream, length)
				else -> throw TODO(type.name)
			}
		}
	}
}