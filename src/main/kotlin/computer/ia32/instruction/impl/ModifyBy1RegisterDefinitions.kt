package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.flag.ArithmeticAdditionFlagOperations
import org.bread_experts_group.computer.ia32.instruction.type.flag.ArithmeticFlagOperations
import org.bread_experts_group.computer.ia32.instruction.type.flag.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.computer.ia32.register.Register

class ModifyBy1RegisterDefinitions : InstructionCluster {
	/**
	 * Opcode: `40+/48+ r(w/d)` |
	 * Instruction: `(IN/DE)C r(16/32)` |
	 * Flags Modified: `OF, SF, ZF, AF, PF` (result dep)
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	abstract class ModifyRegister1(
		opcode: UInt,
		val r32n: String,
		val r16n: String,
		val register: Register,
		s2: String,
		val op: (ULong, ULong) -> ULong
	) : Instruction(opcode, "${s2}c"), ArithmeticFlagOperations {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R32 -> "${this.r32n} [${hex(this.register.tex + 1u)}]"
			AddressingLength.R16 -> "${this.r16n} [${hex(this.register.tx + 1u)}]"
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
			AddressingLength.R32 -> {
				this.setFlagsForLocalOp32(processor, this.register.tex)
				this.register.tex = this.op(this.register.ex, 1u).toUInt()
				this.setFlagsForResult(processor, this.register.tex)
			}

			AddressingLength.R16 -> {
				this.setFlagsForLocalOp16(processor, this.register.tx)
				this.register.tx = this.op(this.register.x, 1u).toUShort()
				this.setFlagsForResult(processor, this.register.tx)
			}

			else -> throw UnsupportedOperationException()
		}

		abstract fun setFlagsForLocalOp16(processor: IA32Processor, r: UShort)
		abstract fun setFlagsForLocalOp32(processor: IA32Processor, r: UInt)
	}

	/**
	 * Opcode: `40+ r(w/d)` |
	 * Instruction: `INC r(16/32)` |
	 * Flags Modified: `OF, SF, ZF, AF, PF` (result dep)
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class Increment(
		opcode: UInt,
		r32n: String,
		r16n: String,
		register: Register
	) : ModifyRegister1(opcode, r32n, r16n, register, "in", ULong::plus), ArithmeticAdditionFlagOperations {
		override fun setFlagsForLocalOp16(processor: IA32Processor, r: UShort) {
			this.setFlagsForOperationR(processor, r, 1u)
		}

		override fun setFlagsForLocalOp32(processor: IA32Processor, r: UInt) {
			this.setFlagsForOperationR(processor, r, 1u)
		}
	}

	/**
	 * Opcode: `48+ r(w/d)` |
	 * Instruction: `DEC r(16/32)` |
	 * Flags Modified: `OF, SF, ZF, AF, PF` (result dep)
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class Decrement(
		opcode: UInt,
		r32n: String,
		r16n: String,
		register: Register
	) : ModifyRegister1(opcode, r32n, r16n, register, "de", ULong::minus), ArithmeticSubtractionFlagOperations {
		override fun setFlagsForLocalOp16(processor: IA32Processor, r: UShort) {
			this.setFlagsForOperationR(processor, r, 1u)
		}

		override fun setFlagsForLocalOp32(processor: IA32Processor, r: UInt) {
			this.setFlagsForOperationR(processor, r, 1u)
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		Increment(0x40u, "eax", "ax", processor.a),
		Increment(0x41u, "ecx", "cx", processor.c),
		Increment(0x42u, "edx", "dx", processor.d),
		Increment(0x43u, "ebx", "bx", processor.b),
		Increment(0x44u, "esp", "sp", processor.sp),
		Increment(0x45u, "ebp", "bp", processor.bp),
		Increment(0x46u, "esi", "si", processor.si),
		Increment(0x47u, "edi", "di", processor.di),
		Decrement(0x48u, "eax", "ax", processor.a),
		Decrement(0x49u, "ecx", "cx", processor.c),
		Decrement(0x4Au, "edx", "dx", processor.d),
		Decrement(0x4Bu, "ebx", "bx", processor.b),
		Decrement(0x4Cu, "esp", "sp", processor.sp),
		Decrement(0x4Du, "ebp", "bp", processor.bp),
		Decrement(0x4Eu, "esi", "si", processor.si),
		Decrement(0x4Fu, "edi", "di", processor.di)
	)
}