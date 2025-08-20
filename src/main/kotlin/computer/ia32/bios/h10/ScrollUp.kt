package org.bread_experts_group.computer.ia32.bios.h10

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.bios.h10.TeletypeOutput.Companion.COLOR_ADDR
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class ScrollUp(val output: TeletypeOutput) : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		if (processor.a.l == 0uL) {
			this.output.position = 0u
			for (position in 0u..<output.characters) {
				processor.computer.setMemoryAt16(
					COLOR_ADDR + (position * 2u),
					processor.b.h.toUShort()
				)
			}
		} else this.output.scroll(processor, processor.a.tl)
	}
}