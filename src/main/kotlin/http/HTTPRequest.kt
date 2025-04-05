package bread_experts_group.http

import bread_experts_group.SmartToString
import bread_experts_group.scanDelimiter
import bread_experts_group.writeString
import java.io.InputStream
import java.io.OutputStream

class HTTPRequest(
	val method: HTTPMethod,
	val path: String,
	val version: String,
	val headers: Map<String, String> = emptyMap()
) : SmartToString() {
	override fun gist(): String = "> ($version) $method $path [HEAD#: ${headers.size}]" + buildString {
		headers.forEach {
			append("\n${it.key}: ${it.value}")
		}
	}

	fun write(stream: OutputStream) {
		stream.writeString("${method.name} $path $version\r\n")
		headers.forEach { (key, value) ->
			stream.writeString("$key:$value\r\n")
		}
		stream.writeString("\r\n")
	}

	companion object {
		fun read(stream: InputStream): HTTPRequest = HTTPRequest(
			HTTPMethod.valueOf(stream.scanDelimiter(" ")),
			stream.scanDelimiter(" "),
			stream.scanDelimiter("\r\n"),
			buildMap {
				while (true) {
					val raw = stream.scanDelimiter("\r\n")
					if (raw == "") break
					var (name, value) = raw.split(":")
					if (value[0] == ' ') value = value.substring(1)
					this[name] = value
				}
			}
		)
	}
}