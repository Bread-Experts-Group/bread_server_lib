package bread_experts_group.http

import bread_experts_group.Writable
import bread_experts_group.writeString
import java.io.OutputStream
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class HTTPResponse private constructor(
	val code: Int,
	val version: String,
	headers: Map<String, String> = emptyMap(),
	val dataLength: Long = 0,
	val data: ByteArray? = null
) : Writable {
	constructor(
		code: Int,
		version: String,
		headers: Map<String, String> = emptyMap(),
		data: String = ""
	) : this(code, version, headers, data.length.toLong(), data.encodeToByteArray())

	constructor(
		code: Int,
		version: String,
		headers: Map<String, String> = emptyMap(),
		data: ByteArray = byteArrayOf(),
	) : this(code, version, headers, data.size.toLong(), data)

	constructor(
		code: Int,
		version: String,
		headers: Map<String, String> = emptyMap(),
		dataSize: Int
	) : this(code, version, headers, dataSize.toLong(), null)

	constructor(
		code: Int,
		version: String,
		headers: Map<String, String> = emptyMap(),
		dataSize: Long
	) : this(code, version, headers, dataSize, null)

	val headers = headers.toMutableMap().also {
		disallowedHeaders.forEach { h ->
			if (it.contains(h)) throw IllegalArgumentException("Do not set $h header")
		}
		it["Server"] = "BEG-BSL"
		it["Date"] = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now())
		it["Content-Length"] = dataLength.toString()
		it["Strict-Transport-Security"] = "max-age=31536000; includeSubDomains; preload"
		it["Alt-Svc"] = "h3=\":443\"; h2=\":443\""
	}

	override fun toString(): String = "($version, <Res>) $code [HEAD#: ${headers.size}]" + buildString {
		headers.forEach {
			append("\n${it.key}: ${it.value}")
		}
	}

	override fun write(stream: OutputStream) {
		stream.writeString("$version $code\r\n")
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