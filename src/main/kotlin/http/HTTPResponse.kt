package org.bread_experts_group.http

import org.bread_experts_group.Writable
import org.bread_experts_group.socket.writeString
import java.io.OutputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class HTTPResponse private constructor(
	val code: Int,
	val version: HTTPVersion,
	headers: Map<String, String> = emptyMap(),
	val dataLength: Long = 0,
	val data: ByteArray? = null
) : Writable {
	constructor(
		code: Int,
		version: HTTPVersion,
		headers: Map<String, String> = emptyMap(),
		data: String = ""
	) : this(code, version, headers, data.length.toLong(), data.encodeToByteArray())

	constructor(
		code: Int,
		version: HTTPVersion,
		headers: Map<String, String> = emptyMap(),
		data: ByteArray = byteArrayOf(),
	) : this(code, version, headers, data.size.toLong(), data)

	constructor(
		code: Int,
		version: HTTPVersion,
		headers: Map<String, String> = emptyMap(),
		dataSize: Int
	) : this(code, version, headers, dataSize.toLong(), null)

	constructor(
		code: Int,
		version: HTTPVersion,
		headers: Map<String, String> = emptyMap(),
		dataSize: Long
	) : this(code, version, headers, dataSize, null)

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

	override fun write(stream: OutputStream) {
		stream.writeString("${version.tag} $code\r\n")
		headers.forEach { (key, value) -> stream.writeString("$key:$value\r\n") }
		stream.writeString("\r\n")
	}

	fun writeWithData(stream: OutputStream) {
		this.write(stream)
		stream.write(data!!)
	}

	companion object {
		val disallowedHeaders = listOf("Server", "Date", "Content-Length", "Strict-Transport-Security", "Alt-Svc")
	}
}