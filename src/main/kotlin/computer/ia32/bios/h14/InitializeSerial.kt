package org.bread_experts_group.computer.ia32.bios.h14

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

object InitializeSerial : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		println("Serial init # ${processor.d.x} [${processor.a.l.toString(2).padStart(8, '0')}]")
		BIOS_RETURN.handle(processor)
		processor.a.tl = 0u
		processor.a.th = 0b00000000u
	}
}