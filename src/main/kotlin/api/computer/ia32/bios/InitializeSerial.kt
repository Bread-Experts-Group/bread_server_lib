package org.bread_experts_group.api.computer.ia32.bios

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class InitializeSerial : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x14u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 0uL
	override fun handle(processor: IA32Processor) {
		println("Serial init # ${processor.d.x} [${processor.a.l.toString(2).padStart(8, '0')}]")
		BIOS_RETURN.handle(processor)
		processor.a.tl = 0u
		processor.a.th = 0b00000000u
	}
}