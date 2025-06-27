package org.bread_experts_group.computer.ia32.bios.h10

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.BIOSInterruptProvider
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn

class TeletypeOutput : BIOSInterruptProvider {
	companion object {
		const val ROWS: ULong = 80u
		const val COLS: ULong = 25u
		val ROWS_D: ULong = 160u
		val RES_D: ULong = this.ROWS * this.COLS
		const val COLOR_ADDR: ULong = 0xB8000u
	}

	private var x: ULong = 0u
	private var y: ULong = 0u

	private val interruptReturn = InterruptReturn()
	override fun handle(processor: IA32Processor) {
		interruptReturn.handle(processor)
		if (this.x == ROWS) {
			this.y++
			this.x = 0u
		}
		if (this.y == COLS) {
			for (pos in COLOR_ADDR..COLOR_ADDR + (RES_D * 2u) step 2) {
				processor.computer.setMemoryAt16(
					pos,
					processor.computer.requestMemoryAt16(pos + ROWS_D)
				)
			}
			this.y--
		}
		if (processor.a.tl == (0x0Au).toUByte()) {
			this.y++
			return
		} else if (processor.a.tl == (0x0Du).toUByte()) {
			this.x = 0u
			return
		}
		processor.computer.setMemoryAt16(
			COLOR_ADDR + (((this.y * ROWS) + this.x) * 2u),
			((processor.a.l shl 8) or 0xFu).toUShort()
		)
		this.x++
	}
}