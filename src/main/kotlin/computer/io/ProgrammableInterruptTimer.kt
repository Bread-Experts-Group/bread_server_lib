package org.bread_experts_group.computer.io

import org.bread_experts_group.computer.BinaryUtil.shl
import org.bread_experts_group.computer.Computer

class ProgrammableInterruptTimer : IODevice {
	override fun read(computer: Computer): UByte {
		TODO("Not yet implemented")
	}

	var collect: UShort = 0u
	var collecting = false
	override fun write(computer: Computer, d: UByte) {
		if (!collecting) {
			collect = d.toUShort()
			collecting = true
			return
		}
		collect = collect or (d.toUShort() shl 8)
		collecting = false
//		Thread.ofVirtual().start {
//			Thread.sleep(2000)
//			while (true) {
//				Thread.sleep(40)
//				(computer.processor as IA32Processor).initiateInterrupt(0x08u)
//			}
//		}
//		TODO("Safer pit")
	}
}