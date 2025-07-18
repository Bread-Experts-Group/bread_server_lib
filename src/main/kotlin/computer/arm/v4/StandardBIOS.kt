package org.bread_experts_group.computer.arm.v4

import org.bread_experts_group.computer.BIOSProvider
import org.bread_experts_group.computer.Computer

class StandardBIOS : BIOSProvider {
	override fun initialize(computer: Computer) {
		val processor = computer.processor as ARMv4Processor
		processor.computer = computer
		processor.biosHooks[0u] = { processor ->
			processor.pc.value = 0x08000008u
		}
	}
}