package coder.format

import org.bread_experts_group.coder.format.iso_bmff.ISOBMFFInputStream
import org.bread_experts_group.coder.format.iso_bmff.box.ISOBMFFBox
import org.bread_experts_group.logging.ColoredLogger
import org.bread_experts_group.stream.FailQuickInputStream
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.InputStream
import kotlin.test.Test

class ISOBMFFInputStreamTest {
	val testFile: InputStream? = this::class.java.classLoader.getResourceAsStream(
		"isobmff/ac01.mp4"
	)
	val testStream = ISOBMFFInputStream(testFile!!)
	val logger = ColoredLogger.newLogger("ISOBMFF InputStream Tests")

	@Test
	fun readParsed() {
		assertDoesNotThrow {
			try {
				var parsed: ISOBMFFBox
				while (true) {
					parsed = testStream.readParsed()
					logger.info(parsed.toString())
				}
			} catch (_: FailQuickInputStream.EndOfStream) {
			}
		}
	}
}