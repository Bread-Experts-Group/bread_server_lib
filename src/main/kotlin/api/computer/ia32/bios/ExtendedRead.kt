package org.bread_experts_group.api.computer.ia32.bios

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import org.bread_experts_group.api.computer.ia32.register.FlagsRegister

class ExtendedRead : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x13u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 0x42uL
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