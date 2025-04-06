package bread_experts_group.http

import bread_experts_group.SmartToString
import bread_experts_group.writeString
import java.io.OutputStream
import java.time.Instant
import java.time.format.DateTimeFormatter

class HTTPResponse private constructor(
	val code: Int,
	val version: String,
	headers: Map<String, String> = emptyMap(),
	val dataLength: Long = 0,
	val data: ByteArray? = null
) : SmartToString() {
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
		it["Date"] = DateTimeFormatter.RFC_1123_DATE_TIME.format(Instant.now())
		it["Content-Length"] = dataLength.toString()
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
	}

	fun writeWithData(stream: OutputStream) {
		this.write(stream)
		stream.write(data!!)
	}

	companion object {
		val disallowedHeaders = listOf("Server", "Date", "Content-Length")
	}
}