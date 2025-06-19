package org.bread_experts_group.computer.ia32.bios.h16

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn

object GetKeystroke : BIOSInterruptProvider {
	private val interruptReturn = InterruptReturn()
	override fun handle(processor: IA32Processor) {
		processor.a.tl = processor.computer.ioMap.getValue(0xB30D0000u).read()
		interruptReturn.handle(processor)
	}
}