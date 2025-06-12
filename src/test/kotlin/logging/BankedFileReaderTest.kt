package logging

import org.bread_experts_group.logging.BankedFileHandler
import org.bread_experts_group.logging.BankedFileReader
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.logging.ColoredLevel
import org.bread_experts_group.logging.ansi_colorspace.ANSI16
import org.bread_experts_group.logging.ansi_colorspace.ANSI16Color
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.test.assertEquals

class BankedFileReaderTest {
	val logger = ColoredHandler.newLogger("Banked File Reader Tests")

	@Test
	fun nextRecord() {
		val coloredLevel = ColoredLevel(
			"TEST_LEVEL",
			ANSI16Color(ANSI16.LIGHT_YELLOW),
			resourceBundleName = "org.bread_experts_group.resource.TestResource_ja"
		)
		run {
			val plaintextLogger = Logger.getLogger("Banked File Reader Tests [Pln]")
			plaintextLogger.useParentHandlers = false
			val logStream = ByteArrayOutputStream()
			val logStreamHandler = ColoredHandler(towards = PrintStream(logStream))
			plaintextLogger.addHandler(logStreamHandler)
			ColoredHandler.coloring = false
			plaintextLogger.log(coloredLevel, "Log write #0 Colored Level")
			plaintextLogger.info("Log write #1 Basic")
			plaintextLogger.warning("Log write #2 Complex メッセージ ỳ ġ \uD83D\uDD25\uD83D\uDD25")
			ColoredHandler.coloring = true
			logger.info("Plaintext log: ${logStream.size()} bytes")
		}
		val handler = BankedFileHandler(
			Path("./testBank_R"),
			Path("./testContent_R"),
			Path("./testTimestamp_R")
		)
		run {
			val bankedLogger = Logger.getLogger("Banked File Reader Tests [Bnk]")
			bankedLogger.useParentHandlers = false
			bankedLogger.addHandler(handler)
			bankedLogger.log(coloredLevel, "Log write #0 Colored Level")
			bankedLogger.info("Log write #1 Basic")
			bankedLogger.warning("Log write #2 Complex メッセージ ỳ ġ \uD83D\uDD25\uD83D\uDD25")
			bankedLogger.removeHandler(handler)
			handler.flush()
			logger.info {
				"Banked log: ${handler.bank.size() + handler.content.size() + handler.timestamp.size()} bytes"
			}
			handler.close()
		}
		val reader = BankedFileReader(handler)
		logger.info(" === READ BACK === ")
		val recordA = reader.nextRecord()
		val recordB = reader.nextRecord()
		val recordC = reader.nextRecord()
		assertEquals(coloredLevel, recordA.level)
		assertEquals(Level.INFO, recordB.level)
		assertEquals(Level.WARNING, recordC.level)
		assertEquals("Log write #0 Colored Level ", recordA.message)
		assertEquals("Log write #1 Basic ", recordB.message)
		assertEquals("Log write #2 Complex メッセージ ỳ ġ \uD83D\uDD25 \uD83D\uDD25 ", recordC.message)
		logger.log(recordA)
		logger.log(recordB)
		logger.log(recordC)
		handler.bankPath.deleteIfExists()
		handler.contentPath.deleteIfExists()
		handler.timestampPath.deleteIfExists()
	}
}