package org.bread_experts_group.computer.ia32.instruction.impl.group.hD1

import org.bread_experts_group.computer.BinaryUtil.shr
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType

object ShiftModRMRightOnce : Instruction(0u, "shr"), ModRM, LogicalArithmeticFlagOperations {
	override fun operands(processor: IA32Processor): String = "${processor.rmD().regMem}, 1"
	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val tmp = memRM.getRMi()
				val result = tmp shr 1
				memRM.setRMi(result)
				this.setFlagsForResult(processor, result)
				processor.flags.setFlag(FlagType.CARRY_FLAG, tmp.takeLowestOneBit() > 0u)
			}

			AddressingLength.R16 -> {
				val tmp = memRM.getRMs()
				val result = tmp shr 1
				memRM.setRMs(result)
				this.setFlagsForResult(processor, result)
				processor.flags.setFlag(FlagType.CARRY_FLAG, tmp.takeLowestOneBit() > 0u)
			}

			else -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}