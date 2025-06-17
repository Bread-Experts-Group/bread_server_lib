package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.flag.ArithmeticSubtractionFlagOperations
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM

class SubtractRegisterFromModRM : Instruction(0x29u, "sub"), ModRM, ArithmeticSubtractionFlagOperations {
	override fun operands(processor: IA32Processor): String = processor.rmD().let { "${it.regMem}, ${it.register}" }
	override fun handle(processor: IA32Processor) {
		val (memRM, register) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				this.setFlagsForOperationR(processor, memRM.getRMi(), register.get().toUInt()).also {
					memRM.setRMi(it)
					this.setFlagsForResult(processor, it)
				}
			}

			AddressingLength.R16 -> {
				this.setFlagsForOperationR(processor, memRM.getRMs(), register.get().toUShort()).also {
					memRM.setRMs(it)
					this.setFlagsForResult(processor, it)
				}
			}

			else -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}