package org.bread_experts_group.http

import org.bread_experts_group.Writable
import org.bread_experts_group.socket.scanDelimiter
import org.bread_experts_group.socket.writeString
import java.io.InputStream
import java.io.OutputStream
import java.net.URLDecoder
import java.net.URLEncoder

class HTTPRequest private constructor(
	val method: HTTPMethod,
	val path: String,
	val version: HTTPVersion,
	val headers: Map<String, String> = emptyMap(),
	@Suppress("unused") val privateTag: Boolean = false
) : Writable {
	constructor(
		method: HTTPMethod,
		path: String,
		version: HTTPVersion,
		headers: Map<String, String> = emptyMap()
	) : this(method, URLEncoder.encode(path, "UTF-8"), version, headers, true)

	override fun toString(): String = "(${version.tag}, <Req>) $method $path [HEAD#: ${headers.size}]" + buildString {
		headers.forEach {
			append("\n${it.key}: ${it.value}")
		}
	}

	override fun write(stream: OutputStream) {
		stream.writeString("${method.name} $path ${version.tag}\r\n")
		headers.forEach { (key, value) ->
			stream.writeString("$key:$value\r\n")
		}
		stream.writeString("\r\n")
	}

	companion object {
		fun read(stream: InputStream): HTTPRequest {
			return HTTPRequest(
				HTTPMethod.safeMapping[stream.scanDelimiter(" ")] ?: HTTPMethod.OTHER,
				stream.scanDelimiter(" ").let {
					try {
						URLDecoder.decode(it, "UTF-8")
					} catch (_: IllegalArgumentException) {
						it
					}
				},
				HTTPVersion.safeMapping[stream.scanDelimiter("\r\n")] ?: HTTPVersion.OTHER,
				buildMap {
					while (true) {
						val raw = stream.scanDelimiter("\r\n")
						if (raw.isEmpty()) break
						var (name, value) = raw.split(':', ignoreCase = true, limit = 2)
						if (value[0] == ' ') value = value.substring(1)
						this[name] = value
					}
				},
				true
			)
		}
	}
}