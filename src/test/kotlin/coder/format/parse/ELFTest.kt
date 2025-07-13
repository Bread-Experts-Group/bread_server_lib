package org.bread_experts_group.coder.format.parse

import org.bread_experts_group.coder.CodingException
import org.bread_experts_group.coder.LazyPartialResult
import org.bread_experts_group.coder.format.parse.elf.ELFParser
import org.bread_experts_group.coder.format.parse.elf.header.ELFWrittenSectionHeader
import org.bread_experts_group.coder.format.parse.elf.header.writer.ELFContextuallyWritable
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Logger
import kotlin.io.path.writeBytes

class ELFTest {
	val tempFile: Path = Files.createTempFile("test", ".elf").also {
		val testStream = this::class.java.classLoader.getResourceAsStream("coder/format/elf/test")!!
		it.writeBytes(testStream.readAllBytes())
	}

	val logger: Logger = ColoredHandler.Companion.newLoggerResourced("tests.elf")

	fun readFrom(fileStream: FileInputStream): List<LazyPartialResult<ELFContextuallyWritable, CodingException>> {
		val testStream = ELFParser(fileStream)
		return testStream.toList()
	}

	fun logAll(
		fileStream: FileInputStream,
		allRead: List<LazyPartialResult<ELFContextuallyWritable, CodingException>>
	) {
		val allRead = allRead.map { it.resultSafe }
		val nameSection = allRead.mapNotNull { it as? ELFWrittenSectionHeader }.firstOrNull { it.sectionNames }
		allRead.forEach {
			if (it is ELFWrittenSectionHeader) {
				val name = if (nameSection != null) {
					fileStream.channel.position(nameSection.contentsPosition + it.nameOffset)
					var buffer = byteArrayOf()
					while (true) {
						val next = fileStream.read()
						if (next == -1 || next == 0) break
						buffer += next.toByte()
					}
					buffer.decodeToString()
				} else "???"
				logger.info("\"$name\" $it")
			} else logger.info(it.toString())
		}
	}

	@Test
	fun read(): Unit = assertDoesNotThrow {
		logger.info("=== READ ===")
		FileInputStream(tempFile.toFile()).use {
			val allRead = readFrom(it)
			logAll(it, allRead)
		}
	}
}