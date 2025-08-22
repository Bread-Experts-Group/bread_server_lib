package org.bread_experts_group.computer.ia32.bios.h1A

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import kotlin.math.roundToInt

object GetSystemTicks : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		val time = (ZonedDateTime.now().getLong(ChronoField.MILLI_OF_DAY) * 0.0182).roundToInt()
		processor.a.l = if (time == 0) 1u else 0u
		processor.c.tx = (time ushr 16).toUShort()
		processor.d.tx = time.toUShort()
	}
}