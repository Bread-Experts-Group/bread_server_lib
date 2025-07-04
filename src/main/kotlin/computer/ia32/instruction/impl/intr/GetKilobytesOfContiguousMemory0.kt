package org.bread_experts_group.computer.ia32.instruction.impl.intr

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider

@BIOSInterrupt(0x12u, 0x00u)
object GetKilobytesOfContiguousMemory0 : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		processor.a.tx = 0x27Fu
	}
}