package org.bread_experts_group.computer.ia32.instruction.impl.group.hFF

import org.bread_experts_group.computer.BinaryUtil.shl
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.assembler.Assembler
import org.bread_experts_group.computer.ia32.assembler.AssemblerMemRM.Companion.asmMemRM
import org.bread_experts_group.computer.ia32.instruction.AssembledInstruction
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.flag.ArithmeticAdditionFlagOperations
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM
import java.io.OutputStream

class IncrementModRM : Instruction(0u, "inc"), ModRM, ArithmeticAdditionFlagOperations, AssembledInstruction {
	override fun operands(processor: IA32Processor): String = processor.rmD().regMem
	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val result = this.setFlagsForOperationR(processor, memRM.getRMi(), 1u)
				memRM.setRMi(result)
				this.setFlagsForResult(processor, result)
			}

			AddressingLength.R16 -> {
				val result = this.setFlagsForOperationR(processor, memRM.getRMs(), 1u)
				memRM.setRMs(result)
				this.setFlagsForResult(processor, result)
			}

			else -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
	override val arguments: Int = 1
	override fun acceptable(assembler: Assembler, from: ArrayDeque<String>): Boolean {
		return from[0].asmMemRM(assembler) != null
	}

	override fun produce(assembler: Assembler, into: OutputStream, from: ArrayDeque<String>) {
		into.write(0xFF)
		val register = from.removeFirst().asmMemRM(assembler)!!
		if (register.address != null) TODO("mem")
		var selected: UByte = 0b11000000u
		selected = selected or (register.register!!.regBits() shl 3)
		into.write(selected.toInt())
	}
}