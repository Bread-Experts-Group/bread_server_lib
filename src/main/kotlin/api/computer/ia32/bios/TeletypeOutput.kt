package org.bread_experts_group.api.computer.ia32.bios

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN

class TeletypeOutput : StandardBIOSInterruptProvider {
	companion object {
		const val COLOR_ADDR: ULong = 0xB8000u
	}

	override lateinit var bios: StandardBIOS
	override val int: UByte = 0x10u
	override fun matches(processor: IA32Processor): Boolean = processor.a.h == 0xEuL

	var position = 0u
	val rows = 25u
	val cols = 80u
	val characters: UInt
		get() = rows * cols

	fun writeCharacter(processor: IA32Processor, char: UByte, attribute: UByte) {
		when (Char(char.toUShort())) {
			'\n' -> this.position = ((this.position.floorDiv(this.cols)) + 1u) * this.cols
			'\r' -> this.position = this.position.floorDiv(this.cols) * this.cols

			else -> {
				processor.computer.setMemoryAt16(
					COLOR_ADDR + (this.position++ * 2u),
					((char.toUInt() shl 8) or attribute.toUInt()).toUShort()
				)
			}
		}
		if (this.position >= (this.cols * this.rows)) scroll(processor, 1u)
	}

	fun scroll(processor: IA32Processor, n: UByte) {
		val topMost = 0u..<(this.characters - this.cols)
		repeat(n.toInt()) {
			for (position in 0u..<this.characters) {
				processor.computer.setMemoryAt16(
					COLOR_ADDR + (position * 2u),
					if (position in topMost) processor.computer.getMemoryAt16(
						COLOR_ADDR + ((position + this.cols) * 2u),
					) else 0u
				)
			}
		}
		val subtract = this.cols * n
		this.position = if (subtract > this.position) 0u
		else this.position - subtract
	}

	override fun handle(processor: IA32Processor) {
		BIOS_RETURN.handle(processor)
		writeCharacter(processor, processor.a.tl, processor.b.tl)
	}
}