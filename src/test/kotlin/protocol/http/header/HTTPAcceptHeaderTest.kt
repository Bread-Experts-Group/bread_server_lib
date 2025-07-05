package org.bread_experts_group.protocol.http.header

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author Miko Elbrecht
 * @since 2.50.0
 */
class HTTPAcceptHeaderTest {
	@Test
	fun accepted() {
		val accept1 = HTTPAcceptHeader.parse("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		assertEquals(1.0, accept1.accepted("text/html"))
		assertEquals(0.8, accept1.accepted("html"))
		assertEquals(1.0, accept1.accepted("application/xhtml+xml"))
		assertEquals(0.9, accept1.accepted("application/xml"))
		assertEquals(0.8, accept1.accepted("*/*"))
		assertEquals(0.8, accept1.accepted("*"))
		assertEquals(0.8, accept1.accepted("text"))
		assertEquals(0.8, accept1.accepted("text/random"))
		val accept2 = HTTPAcceptHeader.parse("test/format,text/html;q=0.9,any/*")
		assertEquals(1.0, accept2.accepted("*"))
		assertEquals(1.0, accept2.accepted("test/format"))
		assertEquals(1.0, accept2.accepted("format"))
		assertEquals(1.0, accept2.accepted("any/*"))
		assertEquals(1.0, accept2.accepted("any/any"))
		assertEquals(1.0, accept2.accepted("any/random"))
		assertEquals(0.9, accept2.accepted("text/html"))
		assertEquals(0.9, accept2.accepted("html"))
		assertEquals(null, accept2.accepted("test"))
		assertEquals(null, accept2.accepted("text"))
		assertEquals(null, accept2.accepted("text/random"))
		assertEquals(null, accept2.accepted("random"))
	}
}