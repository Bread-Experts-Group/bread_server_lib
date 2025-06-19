package org.bread_experts_group.computer.ia32

import org.bread_experts_group.computer.Computer
import org.bread_experts_group.computer.MemoryModule
import org.bread_experts_group.computer.ia32.assembler.Assembler
import org.bread_experts_group.computer.ia32.bios.StandardBIOS
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class IA32Test {
	val processor: IA32Processor = IA32Processor()
	val memoryModule: MemoryModule = MemoryModule(32u * 1024u * 1024u)
	val computer: Computer = Computer(
		listOf(memoryModule),
		processor,
		StandardBIOS()
	)

	fun assemble16(): ByteArray = assertDoesNotThrow {
		val assembler = Assembler(
			buildString {
				appendLine("org 0x500")
				appendLine("bits 16")
				appendLine("test16:")
				appendLine(" xor ax, ax")
				appendLine(" inc ax")
				appendLine(" inc ax")
			}.reader()
		)
		assembler.assemble()
	}

	@Test
	fun additionTest() {
		assemble16().also { println(it.toHexString()) }
//		processor.step()
	}
}