package org.bread_experts_group.coder.format

import org.bread_experts_group.coder.format.parse.flac.FLACParser
import org.bread_experts_group.dumpLog
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import java.net.URI

class FLACParserTest {
	val logger = ColoredHandler.newLoggerResourced("tests.mp3")

	@Test
	fun readBase() {
		val parser = FLACParser(
			URI("https://breadexperts.group/music/Minecraft%20(Alpha)/01.%20Key.flac")
				.toURL()
				.openStream()
		)
		parser.dumpLog(logger)
	}
}