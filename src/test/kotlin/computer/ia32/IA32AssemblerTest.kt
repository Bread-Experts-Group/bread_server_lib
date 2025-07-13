package org.bread_experts_group.computer.ia32

import org.bread_experts_group.coder.format.parse.elf.header.*
import org.bread_experts_group.coder.format.parse.elf.header.writer.ELFSectionHeaderWritable
import org.bread_experts_group.coder.format.parse.elf.header.writer.ELFWriter
import org.bread_experts_group.computer.ia32.assembler.Assembler
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.FileOutputStream
import java.util.*

class IA32AssemblerTest {
	val logger = ColoredHandler.newLoggerResourced("tests.ia_32_assembler")

	@Test
	fun assemble32() = assertDoesNotThrow {
		val ascii = "Hello World!\n"
		val entry = 0x080480CCL
		val assembler = Assembler(
			buildString {
				appendLine("bits 32")
				appendLine("org $entry")
				appendLine("hello_world:")
				appendLine(" mov eax, 4")
				appendLine(" mov ebx, 1")
				appendLine(" mov ecx, @hello_world_literal")
				appendLine(" mov edx, ${ascii.length}")
				appendLine(" int 0x80")
				appendLine("exit:")
				appendLine(" mov eax, 1")
				appendLine(" xor ebx, ebx")
				appendLine(" int 0x80")
				appendLine("hello_world_literal:")
				appendLine(" defutf \"$ascii\"")
			}.reader().buffered()
		)
		val assembled = assembler.assemble()
		logger.info("Assembly: ${assembled.toHexString()}")
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
				entry,
				0
			)
		)
		elfWriter.data = assembled
		elfWriter.programHeaders.add(
			ELFProgramHeader(
				ELFProgramHeaderType.PT_LOAD.code,
				EnumSet.of(ELFProgramHeaderFlags.PF_R, ELFProgramHeaderFlags.PF_X),
				entry,
				0,
				0,
				assembled.size.toLong(),
				assembled.size.toLong(),
				0x1000
			)
		)
		elfWriter.sectionHeaders.add(
			ELFSectionHeaderWritable(
				".text",
				ELFSectionHeaderType.SHT_PROGBITS.code,
				EnumSet.of(ELFSectionHeaderFlags.SHF_EXECINSTR, ELFSectionHeaderFlags.SHF_ALLOC),
				entry,
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