package org.bread_experts_group.computer.ia32

import org.bread_experts_group.computer.Computer
import org.bread_experts_group.computer.MemoryModule
import org.bread_experts_group.computer.ia32.bios.StandardBIOS
import org.junit.jupiter.api.Test

class IA32ProcessorTest {
	val processor: IA32Processor = IA32Processor()
	val memoryModule: MemoryModule = MemoryModule(32u * 1024u * 1024u)
	val computer: Computer = Computer(
		listOf(memoryModule),
		processor,
		StandardBIOS()
	)

	@Test
	fun additionTest() {
//		processor.step()
	}
}