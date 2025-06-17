package org.bread_experts_group.computer.bios.h13

import org.bread_experts_group.computer.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn

object InstallationCheck : BIOSInterruptProvider {
	private val interruptReturn = InterruptReturn()
	override fun handle(processor: IA32Processor) {
		interruptReturn.handle(processor)
		this.setOK(processor)
		processor.b.tx = 0xAA55u
		processor.a.th = 0x30u
		processor.c.tx = 0b111u
	}
}