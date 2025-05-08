package org.bread_experts_group.http

import org.bread_experts_group.CharacterWritable
import java.io.Writer
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class HTTPResponse(
	val code: Int,
	val version: HTTPVersion,
	headers: Map<String, String> = emptyMap(),
	val dataLength: Long = 0
) : CharacterWritable {
	val headers = headers.toMutableMap().also {
		disallowedHeaders.forEach { h ->
			if (it.contains(h)) throw IllegalArgumentException("Do not set $h header")
		}
		it["Server"] = "BEG-BSL"
		it["Date"] = DateTimeFormatter.RFC_1123_DATE_TIME.format(
			ZonedDateTime.ofInstant(
				Instant.now(),
				ZoneOffset.UTC
			)
		)
		it["Content-Length"] = dataLength.toString()
		it["Strict-Transport-Security"] = "max-age=31536000; includeSubDomains; preload"
		it["Alt-Svc"] = "http/1.1=\":443\""
	}

	override fun toString(): String = "($version, <Res>) $code [HEAD#: ${headers.size}]" + buildString {
		headers.forEach {
			append("\n${it.key}: ${it.value}")
		}
	}

	override fun write(writer: Writer) {
		writer.write("${version.tag} $code\r\n")
		headers.forEach { (key, value) -> writer.write("$key:$value\r\n") }
		writer.write("\r\n")
	}

	companion object {
		val disallowedHeaders = listOf("Server", "Date", "Content-Length", "Strict-Transport-Security", "Alt-Svc")
	}
}