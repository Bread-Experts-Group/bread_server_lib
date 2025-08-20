package org.bread_experts_group.computer.ia32.bios.h16

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import org.bread_experts_group.computer.ia32.register.FlagsRegister

object CheckKeystroke : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		val codes = processor.computer.keyboard.scancodes
		processor.flags.setFlag(FlagsRegister.FlagType.ZERO_FLAG, codes.isEmpty())
		if (codes.isNotEmpty()) {
			processor.a.th = codes.first()
			processor.a.tl = processor.computer.keyboard.scancodeToChar(codes.first())
		}
	}
}