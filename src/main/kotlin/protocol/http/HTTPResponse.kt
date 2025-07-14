package org.bread_experts_group.protocol.http

import org.bread_experts_group.buildDate
import org.bread_experts_group.stream.LongInputStream
import org.bread_experts_group.version
import java.io.InputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class HTTPResponse(
	val to: HTTPRequest,
	val code: Int,
	headers: Map<String, String> = emptyMap(),
	val data: InputStream = InputStream.nullInputStream(),
	rawHeaders: Boolean = false
) {
	val headers: Map<String, String> =
		if (rawHeaders) headers else headers.mapKeys { it.key.lowercase() }.toMutableMap().also {
			disallowedHeaders.forEach { h ->
				if (it.contains(h)) throw IllegalArgumentException("Do not set $h header!")
			}
			it["server"] = "BEG-BSL ${version()} @ ${buildDate()}"
			it["date"] = DateTimeFormatter.RFC_1123_DATE_TIME.format(
				ZonedDateTime.ofInstant(
					Instant.now(),
					ZoneOffset.UTC
				)
			)
			it["content-length"] =
				if (data is LongInputStream) data.longAvailable().toString()
				else data.available().toString()
			it["strict-transport-security"] = "max-age=31536000; includeSubDomains; preload"
		}

	override fun toString(): String = "(<Res>) $code [DATA#: ${data.available()}] " + buildString {
		append("[HEAD#: ${headers.size}]")
		headers.forEach {
			append("\n${it.key}: ${it.value}")
		}
	}

	companion object {
		val disallowedHeaders: List<String> = listOf("Server", "Date", "Content-Length", "Strict-Transport-Security")
	}
}