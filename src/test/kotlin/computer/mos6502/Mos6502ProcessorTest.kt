package org.bread_experts_group.computer.mos6502

import org.bread_experts_group.computer.Computer
import org.bread_experts_group.computer.MemoryModule
import kotlin.test.Test

class Mos6502ProcessorTest {
	val processor: Mos6502Processor = Mos6502Processor()
	val memoryModule: MemoryModule = MemoryModule(32u * 2048u)
	val romModule: MemoryModule = MemoryModule(64u * 1024u)
	val computer: Computer = Computer(
		listOf(memoryModule),
		processor,
		DummyBIOS()
	)
	val romStream = this::class.java.classLoader.getResourceAsStream("computer/mos6502/6502_functional_test.bin")!!

	@Test
	fun test() {
		var address = romModule.effectiveAddress!!
		while (true) {
			val next = romStream.read()
			if (next == -1) break
			computer.setMemoryAt(address, next.toUByte())
			address++
		}

		computer.reset()
		while (true) {
			computer.step()
			Thread.sleep(100)
		}
	}
}