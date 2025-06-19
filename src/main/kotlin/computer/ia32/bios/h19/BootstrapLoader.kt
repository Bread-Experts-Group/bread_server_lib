package org.bread_experts_group.computer.ia32.bios.h19

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider

object BootstrapLoader : BIOSInterruptProvider {
	override fun handle(processor: IA32Processor) {
		processor.computer.reset()
		processor.fetch()
		processor.ip.rx--
	}
}