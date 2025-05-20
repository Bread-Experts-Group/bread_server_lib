package org.bread_experts_group.http

import org.bread_experts_group.Writable
import org.bread_experts_group.stream.scanDelimiter
import org.bread_experts_group.stream.writeString
import java.io.InputStream
import java.io.OutputStream
import java.net.URI

class HTTPRequest private constructor(
	val method: HTTPMethod,
	val path: URI,
	val version: HTTPVersion,
	val headers: Map<String, String> = emptyMap()
) : Writable {
	override fun toString(): String = "(${version.tag}, <Req>) $method $path " + buildString {
		append("[HEAD#: ${headers.size}]")
		headers.forEach {
			append("\n${it.key}: ${it.value}")
		}
	}

	override fun write(stream: OutputStream) {
		stream.writeString("${method.name} $path ${version.tag}\r\n")
		headers.forEach { (key, value) -> stream.writeString("$key:$value\r\n") }
		stream.writeString("\r\n")
	}

	companion object {
		fun read(stream: InputStream): HTTPRequest = HTTPRequest(
			HTTPMethod.safeMapping[stream.scanDelimiter(" ")] ?: HTTPMethod.OTHER,
			URI(stream.scanDelimiter(" ").replace(Regex("%(?![0-9a-fA-F]{2})"), "%25")),
			HTTPVersion.safeMapping[stream.scanDelimiter("\r\n")] ?: HTTPVersion.OTHER,
			buildMap {
				while (true) {
					val raw = stream.scanDelimiter("\r\n")
					if (raw.isEmpty()) break
					var (name, value) = raw.split(':', ignoreCase = true, limit = 2)
					if (value[0] == ' ') value = value.substring(1)
					this[name] = value
				}
			}
		)
	}
}