package org.bread_experts_group.computer.ia32.bios

import org.bread_experts_group.computer.ia32.IA32Processor

class BootstrapLoader : StandardBIOSInterruptProvider {
	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x19u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 0uL
	override fun handle(processor: IA32Processor) {
		processor.computer.reset()
		processor.fetch()
		processor.ip.rx--
	}
}