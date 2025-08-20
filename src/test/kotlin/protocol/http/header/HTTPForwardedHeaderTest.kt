package org.bread_experts_group.protocol.old.http.header

import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HTTPForwardedHeaderTest {
	private val logger = ColoredHandler.newLogger("tmp tmp tmp")

	@Test
	fun test() = assertDoesNotThrow {
		fun on(v: String, e: String = v) = assertEquals(
			e,
			HTTPForwardedHeader.parse(v).toString()
		)
		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Forwarded#examples
		on("for=\"_mdn\"")
		on("for=\"[2001:db8:cafe::17]:4711\"", "for=\"[2001:db8:cafe:0:0:0:0:17]:4711\"")
		on("for=192.0.2.60;proto=http;by=203.0.113.43", "by=203.0.113.43;for=192.0.2.60;proto=http")
		on("for=192.0.2.43, for=198.51.100.17")
		on("for=192.0.2.172")
		on("for=192.0.2.43, for=\"[2001:db8:cafe::17]\"", "for=192.0.2.43, for=\"[2001:db8:cafe:0:0:0:0:17]\"")
	}
}