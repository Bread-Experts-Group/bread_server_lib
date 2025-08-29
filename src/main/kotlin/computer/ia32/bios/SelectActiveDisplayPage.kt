package org.bread_experts_group.computer.ia32.bios

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class SelectActiveDisplayPage : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x10u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 5uL
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		println("TODO: PAGE MOV ${processor.a.l}")
	}
}