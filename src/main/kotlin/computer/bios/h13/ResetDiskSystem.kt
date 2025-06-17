package org.bread_experts_group.computer.bios.h13

import org.bread_experts_group.computer.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn

object ResetDiskSystem : BIOSInterruptProvider {
	private val interruptReturn = InterruptReturn()
	override fun handle(processor: IA32Processor) {
		interruptReturn.handle(processor)
		this.setOK(processor)
	}
}