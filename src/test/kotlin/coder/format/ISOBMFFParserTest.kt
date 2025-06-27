package org.bread_experts_group.coder.format

import org.bread_experts_group.coder.format.iso_bmff.ISOBMFFParser
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.InputStream
import java.util.logging.Logger
import kotlin.test.Test

class ISOBMFFParserTest {
	val testFile: InputStream? = this::class.java.classLoader.getResourceAsStream(
		"coder/format/isobmff/ac01.mp4"
	)
	val testStream: ISOBMFFParser = ISOBMFFParser(testFile!!)
	val logger: Logger = ColoredHandler.newLoggerResourced("tests.isobmff")

	@Test
	fun readParsed(): Unit = assertDoesNotThrow {
		while (testStream.hasRemaining()) {
			val parsed = testStream.readParsed()
			logger.info(parsed.toString())
		}
	}
}