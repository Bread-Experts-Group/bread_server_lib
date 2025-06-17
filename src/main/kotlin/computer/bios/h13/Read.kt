package org.bread_experts_group.computer.bios.h13

import org.bread_experts_group.computer.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn

object Read : BIOSInterruptProvider {
	private val interruptReturn = InterruptReturn()
	override fun handle(processor: IA32Processor) {
		val (primary, bootEntry) = processor.computer.disc!!.getBoot()
		val isoStart = (bootEntry.loadRBA * primary.logicalBlockSize).toULong()
		val loc = isoStart + (((processor.d.h * 18u) + (processor.c.l - 1u)) * 512u)
		processor.decoding.loadDiscIntoMemory(
			loc,
			loc + (processor.a.l * 512u),
			processor.es.offset(processor.b.x)
		)

		interruptReturn.handle(processor)
//		processor.flags.setFlag(FlagType.CARRY_FLAG, false)
//		processor.a.h = 0x00u
		this.setError(processor, 0x04u)
	}
}