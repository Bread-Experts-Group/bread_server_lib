package org.bread_experts_group.coder.format.parse

import org.bread_experts_group.coder.format.parse.id3.frame.ID3PictureFrame2
import org.bread_experts_group.coder.format.parse.id3.frame.ID3PictureFrame3
import org.bread_experts_group.coder.format.parse.mp3.MP3Parser
import org.bread_experts_group.coder.format.parse.mp3.frame.MP3ID3Frame
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.testBase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.InputStream
import kotlin.io.path.writeBytes

class MP3ParserTest {
	val testFile: InputStream? = this::class.java.classLoader.getResourceAsStream(
		"coder/format/mp3/8dd8d4dc-7803-4257-a252-30e72d9d6c55.mp3"
	)
	val testStream = MP3Parser().setInput(testFile!!)
	val logger = ColoredHandler.newLoggerResourced("tests.mp3")

	@Test
	fun readParsed() = assertDoesNotThrow {
		var i = 0
		testStream.forEach {
			val frame = it.resultSafe
			when (frame) {
				is MP3ID3Frame -> {
					logger.info(frame.id3.toString())
					val frames = frame.id3.toList()
					frames.forEach { frame -> logger.info(frame.toString()) }
					frames.firstNotNullOfOrNull { frame -> frame.resultSafe as? ID3PictureFrame3 }?.let { frame ->
						testBase.resolve("apic.${frame.mimeType.substringAfter('/')}")
							.writeBytes(frame.data)
					}
					frames.firstNotNullOfOrNull { frame -> frame.resultSafe as? ID3PictureFrame2 }?.let { frame ->
						testBase.resolve("pic.${frame.imageType}").writeBytes(frame.data)
					}
				}

				else -> {
					if (i > 250) return@forEach
					i++
					logger.info("$it")
				}
			}
		}
	}
}