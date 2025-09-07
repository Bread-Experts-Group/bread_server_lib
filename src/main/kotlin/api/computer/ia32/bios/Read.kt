package org.bread_experts_group.api.computer.ia32.bios

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import org.bread_experts_group.api.computer.ia32.register.FlagsRegister

class Read : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x13u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 2uL
	data class FloppyGeometry(
		val sectorsPerTrack: UInt,
		val heads: UInt,
		val bytesPerSector: UInt
	) {
		companion object {
			val floppy3_5_144M = FloppyGeometry(18u, 2u, 512u)
			val floppy5_14_160K = FloppyGeometry(8u, 1u, 512u)
			val floppy5_14_320K = FloppyGeometry(8u, 2u, 512u)
			val floppy5_14_360K = FloppyGeometry(9u, 2u, 512u)
		}
	}

	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		val floppy = processor.computer.floppies.getOrNull(processor.d.l.toInt())
		if (floppy != null) {
			val cylinder = processor.c.th.toUInt()
			val head = processor.d.th.toUInt()
			val sector = processor.c.l and 0b111111u
			if (sector == 0uL) TODO("Invalid sector 0")

			val lba = (cylinder * floppy.second.heads + head) * floppy.second.sectorsPerTrack + (sector - 1u)
			val startByte = lba * floppy.second.bytesPerSector
			val endByte = (startByte + (processor.a.l.toUInt() * floppy.second.bytesPerSector)) - 1u

			processor.logger.info("Floppy copy, cylinder # $cylinder, sector $sector, head $head")
			processor.decoding.loadFloppyIntoMemory(
				floppy.first,
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