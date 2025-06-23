package org.bread_experts_group.computer.ia32.instruction.impl.intr

import org.bread_experts_group.command_line.stringToInt
import org.bread_experts_group.command_line.stringToIntOrNull
import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.assembler.Assembler
import org.bread_experts_group.computer.ia32.instruction.AssembledInstruction
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate8
import java.io.OutputStream

class FireSoftwareInterrupt : Instruction(0xCDu, "int"), Immediate8, AssembledInstruction {
	override fun operands(processor: IA32Processor): String = hex(processor.imm8())
	override fun handle(processor: IA32Processor): Unit = processor.initiateInterrupt(processor.imm8())

	override val arguments: Int = 1
	override fun acceptable(assembler: Assembler, from: ArrayDeque<String>): Boolean {
		return stringToIntOrNull(0x00..0xFF)(from.first()) != null
	}

	override fun produce(assembler: Assembler, into: OutputStream, from: ArrayDeque<String>) {
		into.write(opcode.toInt())
		into.write(stringToInt(0x00..0xFF)(from.removeFirst()))
	}
}