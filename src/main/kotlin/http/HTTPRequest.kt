package bread_experts_group.http

import bread_experts_group.SmartToString
import bread_experts_group.scanDelimiter
import bread_experts_group.writeString
import java.io.InputStream
import java.io.OutputStream
import java.net.URLDecoder
import java.net.URLEncoder

class HTTPRequest private constructor(
	val method: HTTPMethod,
	val path: String,
	val version: String,
	val headers: Map<String, String> = emptyMap(),
	@Suppress("unused") val privateTag: Boolean = false
) : SmartToString() {
	constructor(
		method: HTTPMethod,
		path: String,
		version: String,
		headers: Map<String, String> = emptyMap()
	) : this(method, URLEncoder.encode(path, "UTF-8"), version, headers, true)

	override fun gist(): String = "($version, <Req>) $method $path [HEAD#: ${headers.size}]" + buildString {
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
		fun read(stream: InputStream): HTTPRequest {
			return HTTPRequest(
				HTTPMethod.valueOf(stream.scanDelimiter(" ")),
				URLDecoder.decode(stream.scanDelimiter(" "), "UTF-8"),
				stream.scanDelimiter("\r\n"),
				buildMap {
					while (true) {
						val raw = stream.scanDelimiter("\r\n")
						if (raw == "") break
						var (name, value) = raw.split(":")
						if (value[0] == ' ') value = value.substring(1)
						this[name] = value
					}
				},
				true
			)
		}
	}
}