package org.bread_experts_group.computer.arm.v4

import org.bread_experts_group.computer.Computer
import org.bread_experts_group.computer.MemoryModule
import org.junit.jupiter.api.Test

class ARMv4ProcessorTest {
	val processor: ARMv4Processor = ARMv4Processor()

	// Video
	val paletteModule: MemoryModule = MemoryModule(0x3FFu, 0x05000000u)

	// ROM
	val romModule: MemoryModule = MemoryModule(0x1FFFFFFu, 0x08000000u)
	val computer: Computer = Computer(
		listOf(
			paletteModule,
			romModule
		),
		processor,
		StandardBIOS()
	)
	val romStream = this::class.java.classLoader.getResourceAsStream(
		"computer/arm/v4/BtnTest.gba"
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