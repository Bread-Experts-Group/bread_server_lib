package org.bread_experts_group.computer.ia32.bios

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class GetVideoMode : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x10u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 0xFuL
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		processor.a.h = bios.teletype.cols.toULong()
		processor.a.l = 0x02u
		processor.b.h = 0u
	}
}