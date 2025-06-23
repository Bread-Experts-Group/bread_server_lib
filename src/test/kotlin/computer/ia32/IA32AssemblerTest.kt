package org.bread_experts_group.computer.ia32

import org.bread_experts_group.coder.format.elf.header.*
import org.bread_experts_group.coder.format.elf.header.writer.ELFSectionHeaderWritable
import org.bread_experts_group.coder.format.elf.header.writer.ELFWriter
import org.bread_experts_group.computer.ia32.assembler.Assembler
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import java.io.FileOutputStream

class IA32AssemblerTest {
	val logger = ColoredHandler.newLoggerResourced("tests.ia_32_assembler")

	@Test
	fun assemble16() {
		val assembler = Assembler(
			buildString {
				appendLine("bits 32")
				appendLine("org 0x080480CC")
				appendLine("test32:")
				appendLine(" mov eax, 1")
				appendLine(" mov ebx, 16#CAFE#")
				appendLine(" int 0x80")
			}.reader()
		)
		val assembled = assembler.assemble()
		logger.info("R: ${assembled.toHexString()}")
		val elfWriter = ELFWriter(
			ELFHeader(
				ELFHeaderBits.BIT_32,
				ELFHeaderEndian.LITTLE,
				1,
				ELFApplicationBinaryInterface.SYSTEM_V.code,
				0,
				ELFObjectType.ET_EXEC.code,
				ELFInstructionSetArchitecture.X86.code,
				1,
				0x080480CC,
				0
			)
		)
		elfWriter.data = assembled
		elfWriter.programHeaders.add(
			ELFProgramHeader(
				ELFProgramHeaderType.PT_LOAD.code,
				setOf(ELFProgramHeaderFlags.PF_R, ELFProgramHeaderFlags.PF_X),
				0x080480CC,
				0,
				0,
				9,
				9,
				0x1000
			)
		)
		elfWriter.sectionHeaders.add(
			ELFSectionHeaderWritable(
				".text",
				ELFSectionHeaderType.SHT_PROGBITS.code,
				setOf(ELFSectionHeaderFlags.SHF_EXECINSTR, ELFSectionHeaderFlags.SHF_ALLOC),
				0x080480CC,
				0,
				assembled.size.toLong(),
				0,
				0,
				0x1000,
				0
			)
		)
		val output = FileOutputStream("./src/test/resources/coder/format/elf/test")
		elfWriter.writeFull(output)
	}
}