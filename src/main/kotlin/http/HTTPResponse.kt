package bread_experts_group.http

import bread_experts_group.SmartToString
import bread_experts_group.writeString
import java.io.OutputStream

class HTTPResponse(
	val code: Int,
	val version: String,
	val headers: Map<String, String> = mapOf(
		"Content-Length" to "0",
		"Connection" to "close"
	)
) : SmartToString() {
	override fun gist(): String = "< ($version) $code [HEAD#: ${headers.size}]" + buildString {
		headers.forEach {
			append("\n${it.key}: ${it.value}")
		}
	}

	fun write(stream: OutputStream) {
		stream.writeString("$version $code\r\n")
		headers.forEach { (key, value) ->
			stream.writeString("$key:$value\r\n")
		}
		stream.writeString("\r\n")
	}
}