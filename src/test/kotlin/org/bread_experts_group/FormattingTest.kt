package org.bread_experts_group.org.bread_experts_group

import org.bread_experts_group.formatMetric
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class FormattingTest {
	@Test
	fun formatSITest() {
		assertEquals("0.00 q", 0.0.formatMetric())
		assertEquals("1.00 ", 1.0.formatMetric())
		assertEquals("1.00 da", 10.0.formatMetric())
		assertEquals("1.00 h", 100.0.formatMetric())
		assertEquals("1.00 k", 1000.0.formatMetric())
		assertEquals("10.00 k", 10000.0.formatMetric())
		assertEquals("100.00 k", 100000.0.formatMetric())
		assertEquals("1.00 M", 1000000.0.formatMetric())
		assertEquals("10.00 M", 10000000.0.formatMetric())
		assertEquals("100.00 M", 100000000.0.formatMetric())
		assertEquals("1.00 G", 1000000000.0.formatMetric())
	}
}