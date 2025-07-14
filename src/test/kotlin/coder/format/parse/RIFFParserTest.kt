package org.bread_experts_group.coder.format.parse

import org.bread_experts_group.coder.format.parse.riff.RIFFParser
import org.bread_experts_group.coder.format.parse.riff.chunk.RIFFAudioFormatChunk
import org.bread_experts_group.coder.format.parse.riff.chunk.RIFFContainerChunk
import org.bread_experts_group.dumpLog
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

class RIFFParserTest {
	val testFile = this::class.java.classLoader.getResource("coder/format/riff/test_smp_01_卵とじ.wav")!!
	val logger = ColoredHandler.newLoggerResourced("tests.riff")

	@Test
	fun read() = assertDoesNotThrow {
		RIFFParser().setInput(testFile.openStream()).dumpLog(logger)
		val file = (RIFFParser().setInput(testFile.openStream()).first().resultSafe as RIFFContainerChunk)
			.toList()
			.map { it.resultSafe }
		val format = file.firstNotNullOf { it as? RIFFAudioFormatChunk }
		val data = file.first { it.tag == "data" }
		val clip = AudioSystem.getClip()
		clip.open(
			AudioFormat(
				format.sampleRate.toFloat(),
				format.bitsPerSample,
				format.numberOfChannels,
				false,
				false
			),
			data.data,
			0,
			data.data.size
		)
		clip.start()
		Thread.sleep(5000)
		clip.close()
	}
}