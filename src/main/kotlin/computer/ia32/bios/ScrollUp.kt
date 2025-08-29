package org.bread_experts_group.computer.ia32.bios

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class ScrollUp : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x10u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 6uL
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		if (processor.a.l == 0uL) {
			this.bios.teletype.position = 0u
			for (position in 0u..<bios.teletype.characters) {
				processor.computer.setMemoryAt16(
					TeletypeOutput.Companion.COLOR_ADDR + (position * 2u),
					processor.b.h.toUShort()
				)
			}
		} else this.bios.teletype.scroll(processor, processor.a.tl)
	}
}