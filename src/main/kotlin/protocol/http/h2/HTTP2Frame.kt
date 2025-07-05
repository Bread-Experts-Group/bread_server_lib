package org.bread_experts_group.protocol.http.h2

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.hex
import org.bread_experts_group.stream.*
import java.io.InputStream
import java.io.OutputStream

sealed class HTTP2Frame(
	val type: HTTP2FrameType,
	val identifier: Int
) : Writable {
	override fun toString(): String = "(HTTP/2, Frame) ${type.name}, ${hex(identifier)}"

	class HTTP2ProtocolError(reason: String) : DecodingException(reason)
	class HTTP2FrameSizeError(reason: String) : DecodingException(reason)
	class HTTP2FlowControlError(reason: String) : DecodingException(reason)

	abstract fun collectFlags(): Int
	override fun write(stream: OutputStream) {
		stream.write24(this.computeSize().toInt())
		stream.write(this.type.code)
		stream.write(collectFlags())
		stream.write32(identifier)
	}

	companion object {
		fun read(
			stream: InputStream,
			setDynamic: List<Pair<String, String>>
		): HTTP2Frame {
			val length = stream.read24()
			val typeRaw = stream.read()
			val flagsRaw = stream.read()
			val identifier = stream.read32()
			return when (HTTP2FrameType.mapping.getValue(typeRaw)) {
				HTTP2FrameType.DATA -> HTTP2DataFrame.read(stream, length, flagsRaw, identifier)
				HTTP2FrameType.SETTINGS -> HTTP2SettingsFrame.read(stream, length, flagsRaw, identifier)
				HTTP2FrameType.WINDOW_UPDATE -> HTTP2WindowUpdateFrame.read(stream, length, identifier)
				HTTP2FrameType.HEADERS -> HTTP2HeaderFrame.read(stream, length, flagsRaw, identifier, setDynamic)
				HTTP2FrameType.PRIORITY -> TODO("HTTP/2 Frame: Priority")
				HTTP2FrameType.STOP_STREAM -> HTTP2StopStreamFrame.read(stream, length, identifier)
				HTTP2FrameType.PUSH_PROMISE -> TODO("HTTP/2 Frame: Push, Promise")
				HTTP2FrameType.PING -> TODO("HTTP/2 Frame: Ping")
				HTTP2FrameType.SHUTDOWN -> HTTP2ShutdownFrame.read(stream, length, identifier)
				HTTP2FrameType.CONTINUATION -> TODO("HTTP/2 Frame: Continuation")
			}
		}
	}
}