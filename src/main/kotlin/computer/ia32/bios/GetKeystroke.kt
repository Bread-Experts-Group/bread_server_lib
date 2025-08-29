package org.bread_experts_group.computer.ia32.bios

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class GetKeystroke : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x16u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 0uL
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		val code = processor.computer.keyboard.scancodes.take()
		processor.a.th = code
		processor.a.tl = processor.computer.keyboard.scancodeToChar(code)
	}
}