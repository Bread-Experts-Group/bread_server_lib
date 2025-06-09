package org.bread_experts_group.http

import org.bread_experts_group.stream.Writable
import org.bread_experts_group.stream.writeString
import java.io.InputStream
import java.io.OutputStream
import java.net.URI

class HTTPRequest(
	val method: HTTPMethod,
	val path: URI,
	val version: HTTPVersion,
	val headers: Map<String, String> = emptyMap(),
	val data: InputStream
) : Writable {
	override fun toString(): String = "(${version.tag}, <Req>) $method $path " + buildString {
		append("[HEAD#: ${headers.size}]")
		headers.forEach { append("\n${it.key}: ${it.value}") }
	}

	override fun write(stream: OutputStream) {
		stream.writeString("${method.name} $path ${version.tag}\r\n")
		headers.forEach { (key, value) -> stream.writeString("$key:$value\r\n") }
		stream.writeString("\r\n")
	}
}