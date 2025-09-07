package org.bread_experts_group.api.computer.ia32.bios

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import org.bread_experts_group.api.computer.ia32.register.FlagsRegister

class GetDriveParameters : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x13u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 8uL
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		processor.a.h = 0x01u
		processor.flags.setFlag(FlagsRegister.FlagType.CARRY_FLAG, false)
		println("Drive param ${processor.d.l}")
	}
}