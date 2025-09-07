package org.bread_experts_group.api.computer.ia32.bios

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import org.bread_experts_group.coder.Flaggable.Companion.from

class TeletypeOutputString : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x10u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 0x13uL
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		if (processor.b.h > 0u) TODO("Page nr")
		val flags = TeletypeOutputStringFlags.entries.from(processor.a.l.toLong())
		if (!flags.contains(TeletypeOutputStringFlags.UPDATE_CURSOR) || flags.size != 1) TODO("Flags")
		bios.teletype.position = ((bios.teletype.cols * processor.d.h) + processor.d.l).toUInt()
		repeat(processor.c.x.toInt()) {
			val character = processor.computer.getMemoryAt(
				processor.es.offset(processor.bp) + it.toULong()
			)

			bios.teletype.writeCharacter(processor, character, processor.b.tl)
		}
	}
}