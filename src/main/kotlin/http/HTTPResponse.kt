package org.bread_experts_group.http

import org.bread_experts_group.Writable
import org.bread_experts_group.stream.writeString
import java.io.OutputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class HTTPResponse(
	val code: Int,
	val version: HTTPVersion,
	headers: Map<String, String> = emptyMap(),
	val dataLength: Long = 0
) : Writable {
	val headers = headers.toMutableMap().also {
		disallowedHeaders.forEach { h ->
			if (it.contains(h)) throw IllegalArgumentException("Do not set $h header!")
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
		// Defaults
		it.putIfAbsent("X-Frame-Options", "DENY")
		it.putIfAbsent("X-Content-Type-Options", "nosniff")
		it.putIfAbsent("Referrer-Policy", "strict-origin-when-cross-origin")
		it.putIfAbsent(
			"Permissions-Policy", "bluetooth=(), ambient-light-sensor=(), attribution-reporting=()" +
					", autoplay=(self), browsing-topics=(), camera=(), compute-pressure=(), cross-origin-isolated=()" +
					", deferred-fetch=(), deferred-fetch-minimal=(), display-capture=(), encrypted-media=()" +
					", fullscreen=(), geolocation=(), gyroscope=(), hid=(), identity-credentials-get=()" +
					", idle-detection=(), local-fonts=(), magnetometer=(), microphone=(), midi=(), otp-credentials=()" +
					", payment=(), picture-in-picture=(self), publickey-credentials-create=()" +
					", publickey-credentials-get=(), screen-wake-lock=(), serial=(), storage-access=(), summarizer=()" +
					", usb=(), web-share=(), window-management=(), xr-spatial-tracking=(), accelerometer=()"
		)
		it.putIfAbsent(
			"Content-Security-Policy",
			"default-src 'self'; upgrade-insecure-requests; block-all-mixed-content"
		)
		it.putIfAbsent("Cross-Origin-Embedder-Policy", "require-corp")
		it.putIfAbsent("Cross-Origin-Resource-Policy", "same-origin")
		it.putIfAbsent("Cross-Origin-Opener-Policy", "same-origin")
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

	companion object {
		val disallowedHeaders = listOf("Server", "Date", "Content-Length", "Strict-Transport-Security", "Alt-Svc")
	}
}