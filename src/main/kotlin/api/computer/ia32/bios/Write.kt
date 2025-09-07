package org.bread_experts_group.api.computer.ia32.bios

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import org.bread_experts_group.api.computer.ia32.register.FlagsRegister
import java.nio.ByteBuffer

class Write : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x13u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 3uL
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

			processor.logger.info("Floppy write, cylinder # $cylinder, sector $sector, head $head")
			floppy.first.position(startByte.toLong())
			val memoryStart = processor.es.offset(processor.b.x)
			val buffer = ByteBuffer.allocate((endByte - startByte).toInt() + 1)
			for (offset in memoryStart..memoryStart + (endByte - startByte)) {
				buffer.put(processor.computer.getMemoryAt(offset).toByte())
			}
			buffer.clear()
			floppy.first.write(buffer)
			processor.a.h = 0u
			processor.flags.setFlag(FlagsRegister.FlagType.CARRY_FLAG, false)
		} else {
			processor.a.h = 0x31u
			processor.a.l = 0u
			processor.flags.setFlag(FlagsRegister.FlagType.CARRY_FLAG, true)
		}
	}
}