package org.bread_experts_group.coder.format

import org.bread_experts_group.coder.format.elf.ELFParser
import org.bread_experts_group.coder.format.elf.header.ELFHeader
import org.bread_experts_group.coder.format.elf.header.ELFProgramHeader
import org.bread_experts_group.coder.format.elf.header.ELFWrittenSectionHeader
import org.bread_experts_group.coder.format.elf.header.writer.ELFContextuallyWritable
import org.bread_experts_group.coder.format.elf.header.writer.ELFSectionHeaderWritable
import org.bread_experts_group.coder.format.elf.header.writer.ELFWriter
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Logger
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeBytes

class ELFTest {
	val tempFile: Path = Files.createTempFile("test", ".elf").also {
		val testStream = this::class.java.classLoader.getResourceAsStream("coder/format/elf/test")!!
		it.writeBytes(testStream.readAllBytes())
	}

	val logger: Logger = ColoredHandler.Companion.newLoggerResourced("tests.elf")

	fun readFrom(fileStream: FileInputStream): List<ELFContextuallyWritable> {
		val testStream = ELFParser(fileStream)
		return testStream.toList()
	}

	fun logAll(fileStream: FileInputStream, allRead: List<ELFContextuallyWritable>) {
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

	@Test
	fun write(): Unit = assertDoesNotThrow {
		logger.info("=== WRITE ===")
		val fileStream = FileInputStream(tempFile.toFile())
		val allRead = FileInputStream(tempFile.toFile()).use {
			val allRead = readFrom(fileStream)
			logAll(fileStream, allRead)
			allRead
		}

		val outputFile = Path("test-out.elf")
		val writer = ELFWriter(allRead.first() as ELFHeader)
		writer.programHeaders.addAll(allRead.mapNotNull { it as? ELFProgramHeader })
		writer.sectionHeaders.addAll(allRead.mapNotNull { it as? ELFSectionHeaderWritable })
		FileOutputStream(outputFile.toFile()).use { writer.writeFull(it) }
		FileInputStream(outputFile.toFile()).use {
			val reRead = readFrom(it)
			logger.info("=== WRITE (READBACK) ===")
			logAll(it, reRead)
		}
		// Warning: ELF files read and written back may not be executable, as overlapping data is separated
		outputFile.deleteIfExists()
	}
}