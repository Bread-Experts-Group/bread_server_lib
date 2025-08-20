package org.bread_experts_group.computer.ia32.bios.h13

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import org.bread_experts_group.computer.ia32.register.FlagsRegister

object GetDriveParameters : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		processor.a.h = 0x01u
		processor.flags.setFlag(FlagsRegister.FlagType.CARRY_FLAG, false)
		TODO("INSTALL")
	}
}