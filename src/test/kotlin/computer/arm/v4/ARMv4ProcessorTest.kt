package org.bread_experts_group.computer.arm.v4

import org.bread_experts_group.computer.Computer
import org.bread_experts_group.computer.MemoryModule
import org.junit.jupiter.api.Test

class ARMv4ProcessorTest {
	val processor: ARMv4Processor = ARMv4Processor()
	val memoryModule: MemoryModule = MemoryModule(386u * 1024u)
	val romModule: MemoryModule = MemoryModule(32u * 1024u * 1024u, 0x08000000u)
	val computer: Computer = Computer(
		listOf(memoryModule, romModule),
		processor,
		StandardBIOS()
	)
	val romStream = this::class.java.classLoader.getResourceAsStream(
		"computer/arm/v4/Pokemon - Emerald Version (USA, Europe).gba"
	)!!

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