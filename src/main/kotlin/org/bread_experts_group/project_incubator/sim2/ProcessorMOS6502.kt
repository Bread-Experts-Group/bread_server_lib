package org.bread_experts_group.project_incubator.sim2

import org.bread_experts_group.io.reader.DirectDataSink
import org.bread_experts_group.io.reader.DirectDataSource

class ProcessorMOS6502(
	val memorySource: DirectDataSource<UShort>,
	val memorySink: DirectDataSink<UShort>
) {
	constructor(bus: MemoryBus<UShort>) : this(bus, bus)

	var a: UByte = 0u
	var sp: UShort = 0u
		get() = field or 0b1_00000000u
		set(value) {
			field = value and 0xFFu
		}

	var x: UByte = 0u
	var y: UByte = 0u
	var p: UByte = 0b00000100u
	var pc: UShort = 0x0u

	fun powerReset() {
		this.a = 0u
		this.x = 0u
		this.y = 0u
		this.sp = 0u
		this.p = 0b00000100u
		reset()
	}

	fun reset() {
		this.sp = (this.sp - 3u).toUShort()
		this.pc = memorySource.readU16K(0xFFFCu)
	}

	init {
		powerReset()
	}

	var carry: Boolean
		get() = p and 0b00000001u != UByte.MIN_VALUE
		set(value) {
			if (value) p = p or 0b00000001u
		}
	var zero: Boolean
		get() = p and 0b00000010u != UByte.MIN_VALUE
		set(value) {
			if (value) p = p or 0b00000010u
		}
	var interruptDisable: Boolean
		get() = p and 0b00000100u != UByte.MIN_VALUE
		set(value) {
			if (value) p = p or 0b00000100u
		}
	var decimal: Boolean
		get() = p and 0b00001000u != UByte.MIN_VALUE
		set(value) {
			if (value) p = p or 0b00001000u
		}
	var overflow: Boolean
		get() = p and 0b01000000u != UByte.MIN_VALUE
		set(value) {
			if (value) p = p or 0b01000000u
		}
	var negative: Boolean
		get() = p and 0b10000000u != UByte.MIN_VALUE
		set(value) {
			if (value) p = p or 0b10000000u
		}

	fun step() {
		print("${this.pc.toHexString()} ")
		when (val opcode = memorySource.readU8K(this.pc++)) {
			0x10u.toUByte() -> {
				val relative = memorySource.readS8(this.pc++)
				println("BPL $relative")
				if (!this.negative) this.pc = (this.pc.toInt() + relative).toUShort()
			}

			0x78u.toUByte() -> {
				this.interruptDisable = true
				println("SEI")
			}

			0x8Du.toUByte() -> {
				val absolute = memorySource.readU16K(this.pc)
				memorySink.writeU8(absolute, this.a)
				this.pc = (this.pc + 2u).toUShort()
				println("STA ${absolute.toHexString()}")
			}

			0x9Au.toUByte() -> {
				this.sp = this.x.toUShort()
				println("TXS")
			}

			0xA0u.toUByte() -> {
				this.y = memorySource.readU8K(this.pc++)
				this.negative = this.y.toByte() < 0
				this.zero = this.y == UByte.MIN_VALUE
				println("LDY")
			}

			0xA2u.toUByte() -> {
				this.x = memorySource.readU8K(this.pc++)
				this.negative = this.x.toByte() < 0
				this.zero = this.x == UByte.MIN_VALUE
				println("LDX")
			}

			0xA9u.toUByte() -> {
				this.a = memorySource.readU8K(this.pc++)
				this.negative = this.a.toByte() < 0
				this.zero = this.a == UByte.MIN_VALUE
				println("LDA [${this.a}]")
			}

			0xADu.toUByte() -> {
				val absolute = memorySource.readU16K(this.pc)
				this.a = memorySource.readU8K(absolute)
				this.negative = this.a.toByte() < 0
				this.zero = this.a == UByte.MIN_VALUE
				this.pc = (this.pc + 2u).toUShort()
				println("LDA ${absolute.toHexString()} [${this.a}]")
			}

			0xBDu.toUByte() -> {
				TODO("BD............")
			}

			0xD8u.toUByte() -> {
				this.decimal = false
				println("CLD")
			}

			else -> TODO("Opcode ${opcode.toHexString()}")
		}
	}
}