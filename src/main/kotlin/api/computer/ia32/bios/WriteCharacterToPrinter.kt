package org.bread_experts_group.api.computer.ia32.bios

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class WriteCharacterToPrinter : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x17u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 0uL
	override fun handle(processor: IA32Processor) {
		println("Printer write [${processor.d.x}] # ${Char(processor.a.l.toUShort())} [${processor.a.l}]")
		BIOS_RETURN.handle(processor)
		processor.a.th = 0b00000000u
	}
}