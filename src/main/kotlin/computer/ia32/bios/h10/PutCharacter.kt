package org.bread_experts_group.computer.ia32.bios.h10

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class PutCharacter(val output: TeletypeOutput) : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		if (processor.b.h > 0uL) TODO("Page nr")
		repeat(processor.c.x.toInt()) {
			this.output.writeCharacter(processor, processor.a.tl, processor.b.tl)
		}
	}
}