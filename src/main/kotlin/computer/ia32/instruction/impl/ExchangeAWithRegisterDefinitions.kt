package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.register.Register

class ExchangeAWithRegisterDefinitions : InstructionCluster {
	private class ExchangeAWithRegister(
		opcode: UInt,
		val r32n: String,
		val r16n: String,
		val register: Register
	) : Instruction(opcode, "xchg") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R32 -> "eax, ${this.r32n}"
			AddressingLength.R16 -> "ax, ${this.r16n}"
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
			AddressingLength.R32 -> {
				val temp = processor.a.tex
				processor.a.tex = this.register.tex
				this.register.tex = temp
			}

			AddressingLength.R16 -> {
				val temp = processor.a.tx
				processor.a.tx = this.register.tx
				this.register.tx = temp
			}

			else -> throw UnsupportedOperationException()
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		ExchangeAWithRegister(0x90u, "eax", "ax", processor.a),
		ExchangeAWithRegister(0x91u, "ecx", "cx", processor.c),
		ExchangeAWithRegister(0x92u, "edx", "dx", processor.d),
		ExchangeAWithRegister(0x93u, "ebx", "bx", processor.b),
		ExchangeAWithRegister(0x94u, "esp", "sp", processor.sp),
		ExchangeAWithRegister(0x95u, "ebp", "bp", processor.bp),
		ExchangeAWithRegister(0x96u, "esi", "si", processor.si),
		ExchangeAWithRegister(0x97u, "edi", "di", processor.di)
	)
}