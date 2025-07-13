package org.bread_experts_group.coder.format.parse

import org.bread_experts_group.coder.format.parse.gif.GIFParser
import org.bread_experts_group.dumpLog
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.InputStream
import java.util.logging.Logger
import kotlin.test.Test

class GIFParserTest {
	val testFile: InputStream? = this::class.java.classLoader.getResourceAsStream(
		"coder/format/gif/furry-and-fluffy-furry.gif"
	)
	val testStream: GIFParser = GIFParser(testFile!!)
	val logger: Logger = ColoredHandler.newLoggerResourced("tests.gif")

	@Test
	fun readParsed(): Unit = assertDoesNotThrow {
		testStream.forEach { it.dumpLog(logger) }
	}
}