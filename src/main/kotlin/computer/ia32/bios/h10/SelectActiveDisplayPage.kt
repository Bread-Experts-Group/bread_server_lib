package org.bread_experts_group.computer.ia32.bios.h10

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class SelectActiveDisplayPage(val teletype: TeletypeOutput) : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		println("TODO: PAGE MOV ${processor.a.l}")
	}
}