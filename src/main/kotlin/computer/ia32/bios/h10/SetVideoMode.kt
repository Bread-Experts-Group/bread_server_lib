package org.bread_experts_group.computer.ia32.bios.h10

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class SetVideoMode(val output: TeletypeOutput) : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		println("SET VIDEO MODE ${processor.a.l}")
		BIOS_RETURN.handle(processor)
	}
}