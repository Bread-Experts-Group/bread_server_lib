package org.bread_experts_group.coder.format

import org.bread_experts_group.coder.format.id3.frame.ID3PictureFrame
import org.bread_experts_group.coder.format.mp3.MP3Parser
import org.bread_experts_group.coder.format.mp3.frame.MP3ID3Frame
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import java.io.InputStream
import kotlin.io.path.Path
import kotlin.io.path.writeBytes

class MP3ParserTest {
	val testFile: InputStream? = this::class.java.classLoader.getResourceAsStream(
		"coder/format/mp3/c418_death.mp3"
	)
	val testStream: MP3Parser = MP3Parser(testFile!!)
	val logger = ColoredHandler.newLoggerResourced("tests.mp3")

	@Test
	fun readParsed() {
		testStream.forEach {
			when (it) {
				is MP3ID3Frame -> {
					val frames = it.id3.toList()
					frames.forEach { frame -> logger.info(frame.toString()) }
					frames.firstNotNullOfOrNull { frame -> frame as? ID3PictureFrame }?.let { frame ->
						Path("./apic.${frame.mimeType.substringAfter('/')}")
							.writeBytes(frame.data)
					}
				}

				else -> logger.info("$it")
			}
		}
	}
}