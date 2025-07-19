package org.bread_experts_group.protocol.http

import org.bread_experts_group.channel.EmptyChannel
import org.bread_experts_group.protocol.http.HTTPResponse.Companion.disallowedHeaders
import java.net.URI
import java.nio.channels.ReadableByteChannel

open class HTTPRequest(
	val method: HTTPMethod,
	val path: URI,
	headers: Map<String, String> = emptyMap(),
	val data: ReadableByteChannel = EmptyChannel,
	rawHeaders: Boolean = false
) {
	val headers: MutableMap<String, String> =
		(if (rawHeaders) headers else headers.mapKeys { it.key.lowercase() }.also {
			disallowedHeaders.forEach { h ->
				if (it.contains(h)) throw IllegalArgumentException("Do not set $h header!")
			}
		}).toMutableMap()

	override fun toString(): String = "(<Req>) $method $path " + buildString {
		append("[HEAD#: ${headers.size}]")
		headers.forEach { append("\n${it.key}: ${it.value}") }
	}
}