package org.bread_experts_group.coder.format.parse

import org.bread_experts_group.coder.format.parse.iso_bmff.ISOBMFFParser
import org.bread_experts_group.dumpLog
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.InputStream
import java.util.logging.Logger
import kotlin.test.Test

class ISOBMFFParserTest {
	val testFile: InputStream? = this::class.java.classLoader.getResourceAsStream(
		"coder/format/isobmff/01_simple.mp4"
	)
	val testStream = ISOBMFFParser().setInput(testFile!!)
	val logger: Logger = ColoredHandler.newLoggerResourced("tests.isobmff")

	@Test
	fun readParsed(): Unit = assertDoesNotThrow {
		testStream.forEach { it.dumpLog(logger) }
	}
}