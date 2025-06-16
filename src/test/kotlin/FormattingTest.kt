package org.bread_experts_group

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FormattingTest {
	@Test
	fun truncateSITest() {
		assertEquals("1.00", truncateSI(1))
		assertEquals("10.00", truncateSI(10))
		assertEquals("100.00", truncateSI(100))
		assertEquals("1.00 k", truncateSI(1000))
		assertEquals("10.00 k", truncateSI(10000))
		assertEquals("100.00 k", truncateSI(100000))
		assertEquals("1.00 M", truncateSI(1000000))
		assertEquals("10.00 M", truncateSI(10000000))
		assertEquals("100.00 M", truncateSI(100000000))
	}
}