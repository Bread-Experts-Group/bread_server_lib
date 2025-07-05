package org.bread_experts_group.protocol.http

import java.io.InputStream
import java.net.URI

open class HTTPRequest(
	val method: HTTPMethod,
	val path: URI,
	headers: Map<String, String> = emptyMap(),
	val data: InputStream = InputStream.nullInputStream()
) {
	val headers: Map<String, String> = headers.mapKeys { it.key.lowercase() }

	override fun toString(): String = "(<Req>) $method $path " + buildString {
		append("[DATA#: ${data.available()}] [HEAD#: ${headers.size}]")
		headers.forEach { append("\n${it.key}: ${it.value}") }
	}
}