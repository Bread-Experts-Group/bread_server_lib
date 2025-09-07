package org.bread_experts_group.api.computer.ia32

import org.bread_experts_group.api.computer.ia32.assembler.Assembler
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.testBase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import kotlin.io.path.createParentDirectories

class IA32AssemblerTest {
	val logger = ColoredHandler.newLoggerResourced("tests.ia_32_assembler")

	@Test
	fun assemble32(): Unit = assertDoesNotThrow {
		val entry = 0x500L
		val assembler = Assembler(
			this::class.java.classLoader.getResourceAsStream(
				"computer/ia32/test_basic.asm"
			)!!.bufferedReader()
		)
		val assembled = assembler.assemble()
		this.logger.info("Assembled: ${assembled.toHexString()}")
		val outputTo = testBase.resolve("computer/ia32/test_basic.bin")
		outputTo.createParentDirectories()
		val output = Files.newByteChannel(
			outputTo,
			StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
		)
		output.write(ByteBuffer.wrap(assembled))
		output.position(510L)
		output.write(ByteBuffer.wrap(byteArrayOf(0x55, 0xAA.toByte())))
		output.close()
//		logger.info("Assembly: ${assembled.toHexString()}")
//		val elfWriter = ELFWriter(
//			ELFHeader(
//				ELFHeaderBits.BIT_32,
//				ELFHeaderEndian.LITTLE,
//				1,
//				ELFApplicationBinaryInterface.SYSTEM_V.code,
//				0,
//				ELFObjectType.ET_EXEC.code,
//				ELFInstructionSetArchitecture.X86.code,
//				1,
//				entry,
//				0
//			)
//		)
//		val alpha = ByteBuffer.allocate(12)
//		alpha.order(ByteOrder.LITTLE_ENDIAN)
//		alpha.putInt(0x1BADB002)
//		alpha.putInt(0x00000000)
//		alpha.putInt(0xE4524FFD.toInt())
//		elfWriter.data = assembled + alpha.array()
//		elfWriter.programHeaders.add(
//			ELFProgramHeader(
//				ELFProgramHeaderType.PT_NOTE.code,
//				EnumSet.of(ELFProgramHeaderFlags.PF_R, ELFProgramHeaderFlags.PF_X),
//				0,
//				0,
//				assembled.size.toLong(),
//				alpha.capacity().toLong(),
//				alpha.capacity().toLong(),
//				0x1000
//			)
//		)
//		elfWriter.programHeaders.add(
//			ELFProgramHeader(
//				ELFProgramHeaderType.PT_LOAD.code,
//				EnumSet.of(ELFProgramHeaderFlags.PF_R, ELFProgramHeaderFlags.PF_X),
//				entry,
//				0,
//				0,
//				assembled.size.toLong(),
//				assembled.size.toLong(),
//				0x1000
//			)
//		)
//		elfWriter.sectionHeaders.add(
//			ELFSectionHeaderWritable(
//				".multiboot",
//				ELFSectionHeaderType.SHT_NOTE.code,
//				EnumSet.noneOf(ELFSectionHeaderFlags::class.java),
//				0,
//				assembled.size.toLong(),
//				alpha.capacity().toLong(),
//				0,
//				0,
//				0x1000,
//				0
//			)
//		)
//		elfWriter.sectionHeaders.add(
//			ELFSectionHeaderWritable(
//				".text",
//				ELFSectionHeaderType.SHT_PROGBITS.code,
//				EnumSet.of(ELFSectionHeaderFlags.SHF_EXECINSTR, ELFSectionHeaderFlags.SHF_ALLOC),
//				entry,
//				0,
//				assembled.size.toLong(),
//				0,
//				0,
//				0x1000,
//				0
//			)
//		)
//		val output = FileOutputStream("./src/test/resources/coder/format/elf/test")
//		elfWriter.writeFull(output)
	}
}