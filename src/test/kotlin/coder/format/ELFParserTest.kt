package org.bread_experts_group.coder.format

import org.bread_experts_group.coder.format.elf.ELFInputStream
import org.bread_experts_group.coder.format.elf.header.ELFHeader
import org.bread_experts_group.coder.format.elf.header.ELFSectionHeader
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Logger
import kotlin.io.path.writeBytes

class ELFParserTest {
	val tempFile: Path = Files.createTempFile("test", ".elf").also {
		val testStream = this::class.java.classLoader.getResourceAsStream("coder/format/elf/supercell-wx")!!
		it.writeBytes(testStream.readAllBytes())
	}

	val logger: Logger = ColoredHandler.Companion.newLoggerResourced("tests.elf")

	@Test
	fun read(): Unit = Assertions.assertDoesNotThrow {
		val fileStream = FileInputStream(tempFile.toFile())
		val testStream = ELFInputStream(fileStream)
		val allRead = testStream.readAllParsed()
		val header = allRead.first() as ELFHeader
		val nameSection = allRead.mapNotNull { it as? ELFSectionHeader }[header.sectionNamesSectionIndex]
		allRead.forEach {
			if (it is ELFSectionHeader) {
				fileStream.channel.position(nameSection.fileOffset + it.nameOffset)
				var buffer = byteArrayOf()
				while (true) {
					val next = fileStream.read()
					if (next == -1 || next == 0) break
					buffer += next.toByte()
				}
				logger.info("\"${buffer.decodeToString()}\" $it")
			} else logger.info(it.toString())
		}
	}
}