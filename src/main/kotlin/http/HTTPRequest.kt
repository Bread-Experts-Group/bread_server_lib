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
	@Suppress("unused") val privateTag: Boolean = false,
	val query: String = "",
	val fragment: String = ""
) : Writable {
	constructor(
		method: HTTPMethod,
		path: String,
		version: HTTPVersion,
		headers: Map<String, String> = emptyMap(),
		query: String = "",
		fragment: String = ""
	) : this(method, URLEncoder.encode(path, "UTF-8"), version, headers, true, query, fragment)

	override fun toString(): String = "(${version.tag}, <Req>) $method " + buildString {
		append("$path${if (query.isNotEmpty()) "[#$fragment]" else ""}${if (query.isNotEmpty()) "[?$query]" else ""}")
		append("[HEAD#: ${headers.size}]")
		headers.forEach {
			append("\n${it.key}: ${it.value}")
		}
	}

	override fun write(stream: OutputStream) {
		val pathStr = "$path${if (query.isNotEmpty()) "#$fragment" else ""}${if (query.isNotEmpty()) "?$query" else ""}"
		stream.writeString("${method.name} $pathStr ${version.tag}\r\n")
		headers.forEach { (key, value) ->
			stream.writeString("$key:$value\r\n")
		}
		stream.writeString("\r\n")
	}

	companion object {
		fun read(stream: InputStream): HTTPRequest {
			val method = HTTPMethod.safeMapping[stream.scanDelimiter(" ")] ?: HTTPMethod.OTHER
			var path = stream.scanDelimiter(" ").let {
				try {
					URLDecoder.decode(it, "UTF-8")
				} catch (_: IllegalArgumentException) {
					it
				}
			}
			var fragment = if (path.contains('#', ignoreCase = true)) {
				val split = path.split('#', ignoreCase = true, limit = 2)
				path = split[0]
				split[1]
			} else ""
			val query = if (path.contains('?', ignoreCase = true)) {
				val split = path.split('?', ignoreCase = true, limit = 2)
				path = split[0]
				split[1]
			} else if (fragment.contains('?', ignoreCase = true)) {
				val split = fragment.split('?', ignoreCase = true, limit = 2)
				fragment = split[0]
				split[1]
			} else ""
			return HTTPRequest(
				method,
				path,
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
				true,
				query,
				fragment
			)
		}
	}
}