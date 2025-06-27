package org.bread_experts_group.computer.mos6502

import org.bread_experts_group.computer.Computer
import org.bread_experts_group.computer.Processor
import org.bread_experts_group.computer.mos6502.instruction.Instruction
import org.bread_experts_group.computer.mos6502.instruction.impl.*
import org.bread_experts_group.computer.mos6502.register.ByteRegister
import org.bread_experts_group.computer.mos6502.register.ShortRegister
import org.bread_experts_group.computer.mos6502.register.StatusRegister
import org.bread_experts_group.hex
import org.bread_experts_group.logging.ColoredHandler
import java.util.logging.Logger

/* References
	https://www.masswerk.at/6502/6502_instruction_set.html
	https://www.nesdev.org/wiki/Instruction_reference
	https://en.wikipedia.org/wiki/MOS_Technology_6502#Registers
	https://www.pagetable.com/c64ref/6502/#
*/

class MOS6502Processor : Processor {
	override lateinit var computer: Computer

	// Accumulator
	val a = ByteRegister("a", 0u)

	// Stack Pointer
	val s = ByteRegister("s", 0u)

	// X Index
	val x = ByteRegister("x", 0u)

	// Y Index
	val y = ByteRegister("y", 0u)

	// Program Counter
	var pc: ShortRegister = ShortRegister("pc", 0x400u)

	// Status Register (flags)
	val status: StatusRegister = StatusRegister()
	val biosHooks: MutableMap<UShort, (MOS6502Processor) -> Unit> = mutableMapOf()
	val logger: Logger = ColoredHandler.newLoggerResourced("mos6502_processor")

	val instructionMap: Map<UInt, Instruction> = mapOf(
		0x00u to Break,
		0x4Cu to JMPAbsolute,
		0xD8u to ClearDecimalMode,
		0xA2u to LoadRegisterXFromMemory,
		0x9Au to TransferIndexXToStackPointer,
		0xA9u to LoadAccumulatorWithMemoryImmediate,
		0x8Du to StoreAccumulatorInMemory,
		0xD0u to BranchResultNotZero,
		0xCAu to DecrementX,
		0xF0u to BranchResultZero,
		0xDEu to DecrementMemoryByOne,
		0x30u to BranchIfMinus,
		0xADu to LoadAccumulatorWithMemoryAbsolute,
		0xC9u to CompareMemoryWithAccumulator,
		0xFEu to IncrementMemoryByOne
	)

	override fun reset() {
		a.value = 0u
		s.value = 0u
		x.value = 0u
		y.value = 0u
		pc.value = 0xFFFCu
		status.value = 0u
		status.setFlag(StatusRegister.FlagType.INTERRUPT, true)
	}

	fun fetch(): UByte {
		this.biosHooks[this.pc.value]?.invoke(this)
		return computer.requestMemoryAt(this.pc.value.toULong()).also { this.pc.value++ }
	}

	fun decode(byte: UByte): String {
		val disassembly = StringBuilder(hex((this.pc.value - 1u).toUShort()))
		disassembly.append(":[")
		disassembly.append(byte)
		disassembly.append('/')
		disassembly.append(hex(byte))
		disassembly.append(']')

		val intFetched = byte.toUInt()
		// the pc is stepped after this instruction is run
		// (which means the current address is one ahead when this instruction is being handled),
		// so if you want the current address you'll need to call currentAddr in the instruction
		// (which subtracts the pc by 1 to get the right address)
		val instruction = instructionMap[intFetched]
			?: TODO("Unimplemented instruction(?): ${hex((this.pc.value - 1u).toUShort())} : ${hex(byte)}")

		instruction.handle(this, disassembly)
		return disassembly.toString()
	}

	override fun step() {
		logger.info(this.decode(this.fetch()))
	}
}