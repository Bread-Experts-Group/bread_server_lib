package org.bread_experts_group.computer.ia32.bios

import org.bread_experts_group.computer.ia32.IA32Processor

interface StandardBIOSInterruptProvider {
	var bios: StandardBIOS
	val int: UByte
	fun matches(processor: IA32Processor): Boolean
	fun handle(processor: IA32Processor)
}