package org.bread_experts_group.computer.ia32.bios

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import org.bread_experts_group.computer.ia32.register.FlagsRegister

class ResetDiskSystem : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x13u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 0uL
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		if (processor.computer.floppies.getOrNull(processor.d.l.toInt()) != null) {
			processor.a.h = 0u
			processor.flags.setFlag(FlagsRegister.FlagType.CARRY_FLAG, false)
		} else {
			processor.a.h = 0x31u
			processor.flags.setFlag(FlagsRegister.FlagType.CARRY_FLAG, true)
		}
	}
}