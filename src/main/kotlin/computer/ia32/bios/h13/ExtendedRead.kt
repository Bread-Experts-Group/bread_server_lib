package org.bread_experts_group.computer.ia32.bios.h13

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import org.bread_experts_group.computer.ia32.register.FlagsRegister

object ExtendedRead : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		if (processor.d.tl != (0xE0u).toUByte()) {
			BIOS_RETURN.handle(processor)
			processor.a.h = 0x04u
			processor.flags.setFlag(FlagsRegister.FlagType.CARRY_FLAG, true)
			return
		}
//		val dapAddr = processor.segment.offset(processor.si)
//		val lbs = processor.computer.discPrimaryVolume!!.logicalBlockSize.toULong()
//		val lba = processor.computer.requestMemoryAt64(dapAddr + 8u) * lbs
//		processor.decoding.loadDiscIntoMemory(
//			lba,
//			lba + (processor.computer.requestMemoryAt16(dapAddr + 2u) * lbs),
//			((processor.computer.requestMemoryAt16(dapAddr + 6u) * 0x10u) +
//					processor.computer.requestMemoryAt16(dapAddr + 4u)).toULong()
//		)
		TODO("Ext ")
		BIOS_RETURN.handle(processor)
		processor.a.h = 0u
		processor.flags.setFlag(FlagsRegister.FlagType.CARRY_FLAG, false)
	}
}