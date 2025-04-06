package bread_experts_group.http

import bread_experts_group.SmartToString
import bread_experts_group.writeString
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HTTPResponse(
	val code: Int,
	val version: String,
	headers: Map<String, String> = emptyMap(),
	val data: ByteArray = byteArrayOf()
) : SmartToString() {
	constructor(
		code: Int,
		version: String,
		headers: Map<String, String> = emptyMap(),
		data: String = ""
	) : this(code, version, headers, data.toByteArray())

	val headers = headers.toMutableMap().also {
		disallowedHeaders.forEach { h ->
			if (it.contains(h)) throw IllegalArgumentException("Do not set $h header")
		}
		it["Server"] = "BEG-BSL"
		it["Date"] = DateTimeFormatter.RFC_1123_DATE_TIME.format(LocalDateTime.now())
		it["Content-Length"] = data.size.toString()
	}

	override fun gist(): String = "< ($version) $code [HEAD#: ${headers.size}]" + buildString {
		headers.forEach {
			append("\n${it.key}: ${it.value}")
		}
	}

	fun write(stream: OutputStream) {
		stream.writeString("$version $code\r\n")
		headers.forEach { (key, value) -> stream.writeString("$key:$value\r\n") }
		stream.writeString("\r\n")
		stream.write(data)
	}

	companion object {
		val disallowedHeaders = listOf("Server", "Date", "Content-Length")
	}
}