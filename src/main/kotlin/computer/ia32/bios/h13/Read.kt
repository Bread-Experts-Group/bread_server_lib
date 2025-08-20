package org.bread_experts_group.computer.ia32.bios.h13

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import org.bread_experts_group.computer.ia32.register.FlagsRegister

object Read : BIOSInterruptProvider {
	// 5 (1/4) FP (160 K)
	private const val SECTORS_PER_TRACK = 8u
	private const val HEADS = 1u
	private const val BYTES_PER_SECTOR = 512u
	// 5 (1/4) FP (320 K)
//	private const val SECTORS_PER_TRACK = 8u
//	private const val HEADS = 2u
//	private const val BYTES_PER_SECTOR = 512u

	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		if (processor.computer.floppyURLs[processor.d.l.toInt()] != null) {
			val cylinder = processor.c.th.toUInt()
			val head = processor.d.th.toUInt()
			val sector = processor.c.l and 0b111111u
			if (sector == 0uL) TODO("Invalid sector 0")

			val lba = (cylinder * HEADS + head) * SECTORS_PER_TRACK + (sector - 1u)
			val startByte = lba * BYTES_PER_SECTOR
			val endByte = (startByte + (processor.a.l.toUInt() * BYTES_PER_SECTOR)) - 1u

			processor.logger.info("Floppy copy, cylinder # $cylinder, sector $sector, head $head")
			processor.decoding.loadFloppyIntoMemory(
				processor.d.l.toInt(),
				startByte,
				endByte,
				processor.es.offset(processor.b.x)
			)
			processor.a.h = 0u
			processor.flags.setFlag(FlagsRegister.FlagType.CARRY_FLAG, false)
		} else {
			processor.a.h = 0x31u
			processor.a.l = 0u
			processor.flags.setFlag(FlagsRegister.FlagType.CARRY_FLAG, true)
		}
	}
}