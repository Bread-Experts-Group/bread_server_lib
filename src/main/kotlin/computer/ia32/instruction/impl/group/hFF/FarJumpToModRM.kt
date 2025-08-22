package org.bread_experts_group.computer.ia32.instruction.impl.group.hFF

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM

object FarJumpToModRM : Instruction(0u, "ljmp"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmD().regMem
	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		val (ip, cs) = when (processor.operandSize) {
			AddressingLength.R32 -> processor.computer.getMemoryAt32(memRM.memory!!) to
					processor.computer.getMemoryAt16(memRM.memory + 4u)

			AddressingLength.R16 -> processor.computer.getMemoryAt16(memRM.memory!!).toUInt() to
					processor.computer.getMemoryAt16(memRM.memory + 2u)

			else -> throw UnsupportedOperationException()
		}
		processor.ip.tex = ip
		processor.cs.tx = cs
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}