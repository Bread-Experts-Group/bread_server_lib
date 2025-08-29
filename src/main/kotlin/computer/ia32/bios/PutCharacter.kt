package org.bread_experts_group.computer.ia32.bios

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class PutCharacter : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x10u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 9uL
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		if (processor.b.h > 0uL) TODO("Page nr")
		repeat(processor.c.x.toInt()) {
			this.bios.teletype.writeCharacter(processor, processor.a.tl, processor.b.tl)
		}
	}
}