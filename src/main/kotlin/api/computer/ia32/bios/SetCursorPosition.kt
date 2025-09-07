package org.bread_experts_group.api.computer.ia32.bios

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class SetCursorPosition : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x10u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 2uL
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
//		if (processor.b.h > 0uL) TODO("Page nr")
		bios.teletype.position = ((processor.d.h * bios.teletype.cols) + processor.d.l).toUInt()
	}
}