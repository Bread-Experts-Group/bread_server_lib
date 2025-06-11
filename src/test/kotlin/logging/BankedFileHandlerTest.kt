package logging

import org.bread_experts_group.logging.BankedFileHandler
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class BankedFileHandlerTest {
	val logger = ColoredHandler.newLogger("Banked File Handler Tests")
	val handler = BankedFileHandler(
		Path("./testBank"),
		Path("./testContent"),
		Path("./testTimestamp")
	)

	@Test
	fun publish() = assertDoesNotThrow {
		logger.addHandler(handler)
		repeat(1000) { logger.info("Basic text message") }
		repeat(1000) { logger.info("Complex メッセージ ỳ ġ \uD83D\uDD25\uD83D\uDD25") }
		handler.close()
	}
}