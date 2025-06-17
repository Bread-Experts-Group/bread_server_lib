package org.bread_experts_group.logging

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists

class BankedFileHandlerTest {
	private val logger = ColoredHandler.newLoggerResourced("tests.banked_file_handler")
	private val handler = BankedFileHandler(
		Path("./testBank"),
		Path("./testContent"),
		Path("./testTimestamp")
	)

	@Test
	fun publish(): Unit = assertDoesNotThrow {
		logger.addHandler(handler)
		repeat(1000) { logger.info("Basic text message") }
		repeat(1000) { logger.info("Complex メッセージ ỳ ġ \uD83D\uDD25\uD83D\uDD25") }
		handler.close()
		handler.bankPath.deleteIfExists()
		handler.contentPath.deleteIfExists()
		handler.timestampPath.deleteIfExists()
	}
}