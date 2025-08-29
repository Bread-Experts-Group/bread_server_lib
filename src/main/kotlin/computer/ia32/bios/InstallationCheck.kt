package org.bread_experts_group.computer.ia32.bios

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import org.bread_experts_group.computer.ia32.register.FlagsRegister

class InstallationCheck : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x13u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 0x41uL
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		processor.b.tx = 0xAA55u
		processor.a.th = 0x30u
		processor.c.tx = 0b111u
		processor.flags.setFlag(FlagsRegister.FlagType.CARRY_FLAG, false)
		TODO("INSTALL")
	}
}