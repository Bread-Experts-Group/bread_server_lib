package org.bread_experts_group.computer.ia32.instruction.impl.group.h83

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.computer.ia32.instruction.type.operand.Immediate8
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM

object ORImmediate8ToModRM : Instruction(0u, "or"), ModRM, Immediate8, LogicalArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = "${processor.rmD().regMem}, ${hex(processor.imm8())}"
	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val result = memRM.getRMi() or processor.imm8().toUInt()
				memRM.setRMi(result)
				this.setFlagsForResult(processor, result)
			}

			AddressingLength.R16 -> {
				val result = memRM.getRMs() or processor.imm8().toUShort()
				memRM.setRMs(result)
				this.setFlagsForResult(processor, result)
			}

			else -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}