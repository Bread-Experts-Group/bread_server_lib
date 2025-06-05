package logging

import org.bread_experts_group.logging.ColoredLogger
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import java.util.logging.Level

class ColoredLoggerTest {
	val logger = ColoredLogger.newLogger("Colored Logger Tests")

	@Test
	fun publish() = assertDoesNotThrow {
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
	}
}