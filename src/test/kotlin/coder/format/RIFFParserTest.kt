package org.bread_experts_group.coder.format

import org.bread_experts_group.coder.format.riff.RIFFParser
import org.bread_experts_group.dumpLog
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.InputStream

class RIFFParserTest {
	val testFile: InputStream? = this::class.java.classLoader.getResourceAsStream(
		"coder/format/riff/test_smp_01_卵とじ.wav"
	)
	val testStream: RIFFParser = RIFFParser(testFile!!)
	val logger = ColoredHandler.newLoggerResourced("tests.riff")

	@Test
	fun read() = assertDoesNotThrow {
		testStream.dumpLog(logger)
	}
}