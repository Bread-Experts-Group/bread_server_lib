package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.command_line.stringToInt
import org.bread_experts_group.command_line.stringToIntOrNull
import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.assembler.Assembler
import org.bread_experts_group.computer.ia32.assembler.AssemblerRegister.Companion.asmRegister
import org.bread_experts_group.computer.ia32.instruction.AssembledInstruction
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate16
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate32
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate8
import org.bread_experts_group.computer.ia32.register.Register
import org.bread_experts_group.stream.le
import org.bread_experts_group.stream.write32
import java.io.OutputStream
import kotlin.reflect.KMutableProperty0

class MoveImmediateIntoRegisterDefinitions : InstructionCluster {
	/**
	 * Opcode: `B8+ r(w/d) i(w/d)` |
	 * Instruction: `MOV r(16/32), imm(16/32)` |
	 * Flags Modified: `none`
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class MoveImmediateIntoRegister(
		opcode: UInt,
		val r32n: String,
		val r16n: String,
		val register: Register
	) : Instruction(opcode, "mov"), Immediate32, Immediate16, AssembledInstruction {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R32 -> "${this.r32n}, ${hex(processor.imm32())}"
			AddressingLength.R16 -> "${this.r16n}, ${hex(processor.imm16())}"
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
			AddressingLength.R32 -> this.register.tex = processor.imm32()
			AddressingLength.R16 -> this.register.tx = processor.imm16()
			else -> throw UnsupportedOperationException()
		}

		override val arguments: Int = 2
		override fun acceptable(assembler: Assembler, from: ArrayDeque<String>): Boolean {
			if (assembler.mode != Assembler.BitMode.BITS_32) TODO(assembler.mode.name)
			val register = from[0].asmRegister(assembler)
			if (register == null || register.registerName != this.register.name) return false
			val immediate = stringToIntOrNull()(from[1])
			return immediate != null
		}

		override fun produce(assembler: Assembler, into: OutputStream, from: ArrayDeque<String>) {
			if (assembler.mode != Assembler.BitMode.BITS_32) TODO(assembler.mode.name)
			from.removeFirst()
			into.write(opcode.toInt())
			into.write32(stringToInt()(from.removeFirst()).le())
		}
	}

	/**
	 * Opcode: `B0+ rb ib` |
	 * Instruction: `MOV r8, imm8` |
	 * Flags Modified: `none`
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class MoveImmediateIntoRegister8(
		opcode: UInt,
		val r8n: String,
		val register: KMutableProperty0<ULong>
	) : Instruction(opcode, "mov"), Immediate8 {
		override fun operands(processor: IA32Processor): String = "${this.r8n}, ${hex(processor.imm8())}"
		override fun handle(processor: IA32Processor) {
			this.register.set(processor.imm8().toULong())
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		MoveImmediateIntoRegister(0xB8u, "eax", "ax", processor.a),
		MoveImmediateIntoRegister(0xB9u, "ecx", "cx", processor.c),
		MoveImmediateIntoRegister(0xBAu, "edx", "dx", processor.d),
		MoveImmediateIntoRegister(0xBBu, "ebx", "bx", processor.b),
		MoveImmediateIntoRegister(0xBCu, "esp", "sp", processor.sp),
		MoveImmediateIntoRegister(0xBDu, "ebp", "bp", processor.bp),
		MoveImmediateIntoRegister(0xBEu, "esi", "si", processor.si),
		MoveImmediateIntoRegister(0xBFu, "edi", "di", processor.di),
		MoveImmediateIntoRegister8(0xB0u, "al", processor.a::l),
		MoveImmediateIntoRegister8(0xB1u, "cl", processor.c::l),
		MoveImmediateIntoRegister8(0xB2u, "dl", processor.d::l),
		MoveImmediateIntoRegister8(0xB3u, "bl", processor.b::l),
		MoveImmediateIntoRegister8(0xB4u, "ah", processor.a::h),
		MoveImmediateIntoRegister8(0xB5u, "ch", processor.c::h),
		MoveImmediateIntoRegister8(0xB6u, "dh", processor.d::h),
		MoveImmediateIntoRegister8(0xB7u, "bh", processor.b::h)
	)
}