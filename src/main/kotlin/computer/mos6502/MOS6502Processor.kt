package org.bread_experts_group.computer.mos6502

import org.bread_experts_group.computer.Computer
import org.bread_experts_group.computer.Processor
import org.bread_experts_group.computer.mos6502.register.ByteRegister
import org.bread_experts_group.computer.mos6502.register.ShortRegister
import org.bread_experts_group.computer.mos6502.register.StatusRegister
import org.bread_experts_group.hex
import java.util.concurrent.CountDownLatch

class MOS6502Processor : Processor {
	override lateinit var computer: Computer
	val byteRegisters = arrayOf(
		ByteRegister("a", 0u),
		ByteRegister("p", 0u),
		ByteRegister("s", 0u),
		ByteRegister("x", 0u),
		ByteRegister("y", 0u)
	)

	val pc: ShortRegister = ShortRegister("pc", 0u)
	val status: StatusRegister = StatusRegister()
	val halt: CountDownLatch = CountDownLatch(1)

	override fun reset() {
		byteRegisters.forEach { it.value = 0u }
		pc.value = 0u
		status.value = 0u
		halt.countDown()
	}

	fun fetch(): UByte {
		this.halt.await()
		return this.computer.requestMemoryAt(this.pc.value.toULong()).also { this.pc.value++ }
	}

	fun decode(byte: UByte) {
		TODO(hex(byte))
	}

	override fun step() {
		this.decode(this.fetch())
	}
}