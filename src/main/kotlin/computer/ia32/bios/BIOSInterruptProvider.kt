package org.bread_experts_group.computer.ia32.bios

import org.bread_experts_group.computer.ia32.IA32Processor

interface BIOSInterruptProvider {
	fun handle(processor: IA32Processor)
}