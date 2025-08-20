package org.bread_experts_group.computer.ia32.bios.h17

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

object WriteCharacterToPrinter : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		println("Printer write [${processor.d.x}] # ${Char(processor.a.l.toUShort())} [${processor.a.l}]")
		BIOS_RETURN.handle(processor)
		processor.a.th = 0b00000000u
	}
}