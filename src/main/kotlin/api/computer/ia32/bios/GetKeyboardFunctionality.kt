package org.bread_experts_group.api.computer.ia32.bios

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class GetKeyboardFunctionality : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x16u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 9uL
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		processor.a.l = 0u // TODO extras
	}
}