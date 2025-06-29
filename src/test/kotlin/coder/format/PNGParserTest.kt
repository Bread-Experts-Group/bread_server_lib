package org.bread_experts_group.coder.format

import org.bread_experts_group.coder.format.png.PNGParser
import org.bread_experts_group.dumpLog
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.InputStream
import java.util.logging.Logger
import kotlin.test.Test

class PNGParserTest {
	val testFile: InputStream? = this::class.java.classLoader.getResourceAsStream(
		"coder/format/png/elephant.apng"
	)
	val testStream: PNGParser = PNGParser(testFile!!)
	val logger: Logger = ColoredHandler.Companion.newLoggerResourced("tests.png")

	@Test
	fun readParsed(): Unit = assertDoesNotThrow {
		testStream.forEach { it.dumpLog(logger) }
	}
}