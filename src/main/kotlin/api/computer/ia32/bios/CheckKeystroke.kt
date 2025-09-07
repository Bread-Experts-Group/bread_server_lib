package org.bread_experts_group.api.computer.ia32.bios

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import org.bread_experts_group.api.computer.ia32.register.FlagsRegister

class CheckKeystroke : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x16u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 1uL
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