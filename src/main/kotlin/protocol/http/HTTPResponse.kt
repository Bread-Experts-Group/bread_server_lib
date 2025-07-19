package org.bread_experts_group.protocol.http

import org.bread_experts_group.buildDate
import org.bread_experts_group.channel.EmptyChannel
import org.bread_experts_group.version
import java.nio.channels.ReadableByteChannel
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class HTTPResponse(
	val to: HTTPRequest,
	val code: Int,
	headers: Map<String, String> = emptyMap(),
	val data: ReadableByteChannel = EmptyChannel,
	rawHeaders: Boolean = false
) {
	val headers: MutableMap<String, String> =
		(if (rawHeaders) headers else headers.mapKeys { it.key.lowercase() }.toMutableMap().also {
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
			it["strict-transport-security"] = "max-age=31536000; includeSubDomains; preload"
		}).toMutableMap()

	override fun toString(): String = "(<Res>) $code " + buildString {
		append("[HEAD#: ${headers.size}]")
		headers.forEach {
			append("\n${it.key}: ${it.value}")
		}
	}

	companion object {
		val disallowedHeaders: List<String> = listOf(
			"content-length", "transfer-encoding",
			"server", "date", "strict-transport-security"
		)
	}
}