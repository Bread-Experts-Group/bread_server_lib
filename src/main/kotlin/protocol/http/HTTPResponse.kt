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
			// Defaults
			it.putIfAbsent("x-frame-options", "DENY")
			it.putIfAbsent("x-content-type-options", "nosniff")
			it.putIfAbsent("referrer-policy", "strict-origin-when-cross-origin")
			it.putIfAbsent(
				"permissions-policy", "bluetooth=(), ambient-light-sensor=(), attribution-reporting=()" +
						", autoplay=(self), browsing-topics=(), camera=(), compute-pressure=(), cross-origin-isolated=()" +
						", deferred-fetch=(), deferred-fetch-minimal=(), display-capture=(), encrypted-media=()" +
						", fullscreen=(), geolocation=(), gyroscope=(), hid=(), identity-credentials-get=()" +
						", idle-detection=(), local-fonts=(), magnetometer=(), microphone=(), midi=(), otp-credentials=()" +
						", payment=(), picture-in-picture=(self), publickey-credentials-create=()" +
						", publickey-credentials-get=(), screen-wake-lock=(), serial=(), storage-access=(), summarizer=()" +
						", usb=(), web-share=(), window-management=(), xr-spatial-tracking=(), accelerometer=()"
			)
			it.putIfAbsent(
				"content-security-policy",
				"default-src 'self'; upgrade-insecure-requests; block-all-mixed-content"
			)
			it.putIfAbsent("cross-origin-embedder-policy", "require-corp")
			it.putIfAbsent("cross-origin-resource-policy", "same-origin")
			it.putIfAbsent("cross-origin-opener-policy", "same-origin")
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