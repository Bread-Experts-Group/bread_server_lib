package org.bread_experts_group.http

import org.bread_experts_group.CharacterWritable
import org.bread_experts_group.socket.scanDelimiter
import java.io.InputStreamReader
import java.io.Writer
import java.net.URI

class HTTPRequest private constructor(
	val method: HTTPMethod,
	val path: URI,
	val version: HTTPVersion,
	val headers: Map<String, String> = emptyMap()
) : CharacterWritable {
	override fun toString(): String = "(${version.tag}, <Req>) $method $path " + buildString {
		append("[HEAD#: ${headers.size}]")
		headers.forEach {
			append("\n${it.key}: ${it.value}")
		}
	}

	override fun write(writer: Writer) {
		writer.write("${method.name} $path ${version.tag}\r\n")
		headers.forEach { (key, value) ->
			writer.write("$key:$value\r\n")
		}
		writer.write("\r\n")
	}

	companion object {
		fun read(stream: InputStreamReader): HTTPRequest = HTTPRequest(
			HTTPMethod.safeMapping[stream.scanDelimiter(" ")] ?: HTTPMethod.OTHER,
			URI(stream.scanDelimiter(" ")),
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