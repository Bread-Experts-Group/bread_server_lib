package org.bread_experts_group.computer.ia32.bios.h10

import org.bread_experts_group.coder.Flaggable.Companion.from
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class TeletypeOutputString(val output: TeletypeOutput) : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		if (processor.b.h > 0u) TODO("Page nr")
		val flags = TeletypeOutputStringFlags.entries.from(processor.a.l.toLong())
		if (!flags.contains(TeletypeOutputStringFlags.UPDATE_CURSOR) || flags.size != 1) TODO("Flags")
		output.position = ((output.cols * processor.d.h) + processor.d.l).toUInt()
		repeat(processor.c.x.toInt()) {
			val character = processor.computer.getMemoryAt(
				processor.es.offset(processor.bp) + it.toULong()
			)

			output.writeCharacter(processor, character, processor.b.tl)
		}
	}
}