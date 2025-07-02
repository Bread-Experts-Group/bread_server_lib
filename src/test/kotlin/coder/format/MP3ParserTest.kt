package org.bread_experts_group.coder.format

import org.bread_experts_group.coder.format.mp3.MP3Parser
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.InputStream

class MP3ParserTest {
	val testFile: InputStream? = this::class.java.classLoader.getResourceAsStream(
		"coder/format/mp3/c418_death.mp3"
	)
	val testStream: MP3Parser = MP3Parser(testFile!!)
	val logger = ColoredHandler.newLoggerResourced("tests.mp3")

	@Test
	fun readParsed() {
//		logger.info("${testStream.next?.header}")
	}
}