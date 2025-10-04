package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.BinaryUtil.hex
import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.api.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.register.Register
import org.bread_experts_group.api.computer.ia32.register.SegmentRegister

class PushFromRegisterDefinitions : InstructionCluster {
	/**
	 * Opcode: `50+/9C r(w/d)` |
	 * Instruction: `PUSH r(16/32)` |
	 * Flags Modified: `none`
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	class PushFromRegister(
		opcode: UInt,
		val r32n: String,
		val r16n: String,
		val register: Register
	) : Instruction(opcode, "push") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R32 -> "${this.r32n} [${hex(this.register.tex)}]"
			AddressingLength.R16 -> "${this.r16n} [${hex(this.register.tx)}]"
			else -> throw IllegalArgumentException("Unsupported mode")
		}

		override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
			AddressingLength.R32 -> processor.push32(this.register.tex)
			AddressingLength.R16 -> processor.push16(this.register.tx)
			else -> throw IllegalArgumentException("Unsupported mode")
		}
	}

	/**
	 * Opcode: `0E/16/1E/06/0FA0/0FA8` |
	 * Instruction: `PUSH (C/S/D/E/F/G)S` |
	 * Flags Modified: `none`
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	class PushFromSegmentRegister(
		opcode: UInt,
		val n: Char,
		val register: SegmentRegister
	) : Instruction(opcode, "push") {
		override fun operands(processor: IA32Processor): String = "${this.n}s [${hex(this.register.tx)}]"
		override fun handle(processor: IA32Processor): Unit = processor.push16(this.register.tx)
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		PushFromRegister(0x50u, "eax", "ax", processor.a),
		PushFromRegister(0x51u, "ecx", "cx", processor.c),
		PushFromRegister(0x52u, "edx", "dx", processor.d),
		PushFromRegister(0x53u, "ebx", "bx", processor.b),
		PushFromRegister(0x54u, "esp", "sp", processor.sp),
		PushFromRegister(0x55u, "ebp", "bp", processor.bp),
		PushFromRegister(0x56u, "esi", "si", processor.si),
		PushFromRegister(0x57u, "edi", "di", processor.di),
		PushFromRegister(0x9Cu, "eflags", "flags", processor.flags),
		PushFromSegmentRegister(0x0Eu, 'c', processor.cs),
		PushFromSegmentRegister(0x16u, 's', processor.ss),
		PushFromSegmentRegister(0x1Eu, 'd', processor.ds),
		PushFromSegmentRegister(0x06u, 'e', processor.es),
		PushFromSegmentRegister(0x0FA0u, 'f', processor.fs),
		PushFromSegmentRegister(0x0FA8u, 'g', processor.gs)
	)
}