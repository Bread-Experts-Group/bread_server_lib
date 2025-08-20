package org.bread_experts_group.computer.ia32.bios.h16

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

object GetKeystroke : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		val code = processor.computer.keyboard.scancodes.take()
		processor.a.th = code
		processor.a.tl = processor.computer.keyboard.scancodeToChar(code)
	}
}