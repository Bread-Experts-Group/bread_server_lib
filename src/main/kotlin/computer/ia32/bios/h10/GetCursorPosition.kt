package org.bread_experts_group.computer.ia32.bios.h10

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class GetCursorPosition(val output: TeletypeOutput) : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		if (processor.b.h > 0uL) TODO("Page nr")
		processor.c.h = 0x06u
		processor.c.l = 0x07u
		processor.d.h = output.position.floorDiv(output.cols).toULong()
		processor.d.l = output.position.mod(output.cols).toULong()
	}
}