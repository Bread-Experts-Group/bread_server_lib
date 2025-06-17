package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.register.Register
import org.bread_experts_group.computer.ia32.register.SegmentRegister

class PopIntoRegisterDefinitions : InstructionCluster {
	/**
	 * Opcode: `58+/9D r(w/d)` |
	 * Instruction: `POP r(16/32)` |
	 * Flags Modified: `none`
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class PopIntoRegister(
		opcode: UInt,
		val r32n: String,
		val r16n: String,
		val register: Register
	) : Instruction(opcode, "pop") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R32 -> "${this.r32n} [${hex(processor.pop32())}]"
			AddressingLength.R16 -> "${this.r16n} [${hex(processor.pop16())}]"
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
			AddressingLength.R32 -> this.register.tex = processor.pop32()
			AddressingLength.R16 -> this.register.tx = processor.pop16()
			else -> throw UnsupportedOperationException()
		}
	}

	/**
	 * Opcode: `1F/07/17` |
	 * Instruction: `POP (D/E/S)S` |
	 * Flags Modified: `none`
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class PopIntoSegmentRegister(
		opcode: UInt,
		val n: Char,
		val register: SegmentRegister
	) : Instruction(opcode, "pop") {
		override fun operands(processor: IA32Processor): String = "${this.n}s [${hex(processor.pop16())}]"
		override fun handle(processor: IA32Processor) {
			this.register.tx = processor.pop16()
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		PopIntoRegister(0x58u, "eax", "ax", processor.a),
		PopIntoRegister(0x59u, "ecx", "cx", processor.c),
		PopIntoRegister(0x5Au, "edx", "dx", processor.d),
		PopIntoRegister(0x5Bu, "ebx", "bx", processor.b),
		PopIntoRegister(0x5Cu, "esp", "sp", processor.sp),
		PopIntoRegister(0x5Du, "ebp", "bp", processor.bp),
		PopIntoRegister(0x5Eu, "esi", "si", processor.si),
		PopIntoRegister(0x5Fu, "edi", "di", processor.di),
		PopIntoRegister(0x9Du, "eflags", "flags", processor.flags),
		PopIntoSegmentRegister(0x07u, 'e', processor.es),
		PopIntoSegmentRegister(0x1Fu, 'd', processor.ds),
		PopIntoSegmentRegister(0x17u, 's', processor.ss)
	)
}