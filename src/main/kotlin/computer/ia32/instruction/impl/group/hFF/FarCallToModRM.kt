package org.bread_experts_group.computer.ia32.instruction.impl.group.hFF

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM

object FarCallToModRM : Instruction(0u, "callf"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmD().regMem

	override fun handle(processor: IA32Processor) {
		val (memRM) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R16 -> {
				val ip = processor.computer.getMemoryAt16(memRM.memory!!)
				val cs = processor.computer.getMemoryAt16(memRM.memory + 2u)
				processor.push16(processor.cs.tx)
				processor.push16(processor.ip.tx)
				processor.ip.tex = ip.toUInt()
				processor.cs.tx = cs
			}

			else -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}