package org.bread_experts_group.computer.mos6502

import org.bread_experts_group.computer.BIOSProvider
import org.bread_experts_group.computer.Computer

class DummyBIOS : BIOSProvider {
	override fun initialize(computer: Computer) {
		val processor = computer.processor as MOS6502Processor
		processor.computer = computer
	}
}