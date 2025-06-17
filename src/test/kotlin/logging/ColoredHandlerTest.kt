package org.bread_experts_group.logging

import org.bread_experts_group.command_line.ArgumentParsingError
import org.bread_experts_group.logging.ansi_colorspace.ANSI16
import org.bread_experts_group.logging.ansi_colorspace.ANSI16Color
import org.bread_experts_group.logging.ansi_colorspace.ANSI256Color
import org.bread_experts_group.logging.ansi_colorspace.ANSITrueColor
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.random.Random
import kotlin.random.nextUBytes

class ColoredHandlerTest {
	val logger: Logger = ColoredHandler.newLoggerResourced("tests.colored_handler")

	@OptIn(ExperimentalUnsignedTypes::class)
	@Test
	fun publish(): Unit = assertDoesNotThrow {
		logger.info("Hello world!")
		logger.log(Level.INFO, Exception()) { "Exception message, no cause" }
		logger.log(Level.INFO, Exception(Exception(Exception()))) { "Exception message, chained cause" }
		val exe = Exception()
		val exe2 = Exception(exe)
		exe.initCause(exe2)
		logger.log(Level.INFO, exe) { "Exception message, cycle cause" }
		logger.log(
			object : Level("TEST", 923) {},
			"Weird level"
		)
		ANSI16.entries.forEach {
			val level = ColoredLevel("Test ${it.name} [${it.ordinal}]", ANSI16Color(it))
			logger.log(level, "Test")
		}
		repeat(256) {
			val level = ColoredLevel("Test Palette $it", ANSI256Color(it.toUByte()))
			logger.log(level, "Test")
		}
		repeat(10) {
			val rgb = Random.nextUBytes(3)
			val level = ColoredLevel("Test RGB", ANSITrueColor(rgb[0], rgb[1], rgb[2]))
			logger.log(level, "Random sampling ${rgb[0]}.${rgb[1]}.${rgb[2]}")
		}
		val err = ArgumentParsingError("Test Parse Error", cause = ClassCircularityError())
		logger.log(Level.INFO, err) { "Error message with cause" }
	}
}