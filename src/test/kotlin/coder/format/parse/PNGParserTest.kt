package org.bread_experts_group.coder.format.parse

import org.bread_experts_group.coder.format.parse.png.PNGByteParser
import org.bread_experts_group.coder.format.parse.png.chunk.PNGChunk
import org.bread_experts_group.dumpLog
import org.bread_experts_group.dumpLogSafe
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File
import java.nio.channels.SeekableByteChannel
import java.nio.file.Files
import java.util.logging.Logger
import kotlin.io.path.forEachDirectoryEntry
import kotlin.io.path.isDirectory
import kotlin.test.Test

class PNGParserTest {
	val logger: Logger = ColoredHandler.newLoggerResourced("tests.png")
	val parser = PNGByteParser()

	fun test(at: String, ascertain: (ByteParser<String, PNGChunk, SeekableByteChannel>) -> Unit) {
		val testURL = this::class.java.classLoader.getResource("coder/format/png/$at")
		if (testURL == null || testURL.protocol != "file") return logger.severe("Unable to test")
		File(testURL.path).toPath().forEachDirectoryEntry { path ->
			if (path.isDirectory()) throw Error("Directory in test directory stream [$path]!")
			try {
				logger.info("File $path")
				ascertain(parser.setInput(Files.newByteChannel(path)))
			} catch (_: InvalidInputException) {
				logger.info("Invalid Input")
			}
		}
	}

	@Test
	fun basic() = test("basic") { assertDoesNotThrow { it.dumpLogSafe(logger) } }

	@Test
	fun interlaced() = test("interlaced") { assertDoesNotThrow { it.dumpLogSafe(logger) } }

	@Test
	fun oddSize() = test("odd_size") { assertDoesNotThrow { it.dumpLogSafe(logger) } }

	@Test
	fun background() = test("background") { assertDoesNotThrow { it.dumpLogSafe(logger) } }

	@Test
	fun transparent() = test("transparent") { assertDoesNotThrow { it.dumpLogSafe(logger) } }

	@Test
	fun gamma() = test("gamma") { assertDoesNotThrow { it.dumpLogSafe(logger) } }

	@Test
	fun filter() = test("filter") { assertDoesNotThrow { it.dumpLogSafe(logger) } }

	@Test
	fun palette() = test("palette") { assertDoesNotThrow { it.dumpLogSafe(logger) } }

	@Test
	fun ancillary() = test("ancillary") { assertDoesNotThrow { it.dumpLogSafe(logger) } }

	@Test
	fun ordered() = test("ordered") { assertDoesNotThrow { it.dumpLogSafe(logger) } }

	@Test
	fun zlib() = test("zlib") { assertDoesNotThrow { it.dumpLogSafe(logger) } }

	@Test
	fun corrupt() = test("corrupt") { assertDoesNotThrow { it.dumpLog(logger) } }

	@Test
	fun extra() = test("extra") { assertDoesNotThrow { it.dumpLog(logger) } }

	@Test
	fun large() = test("large") { assertDoesNotThrow { it.dumpLog(logger) } }

	@Test
	fun animated() = test("animated") { assertDoesNotThrow { it.dumpLog(logger) } }
}