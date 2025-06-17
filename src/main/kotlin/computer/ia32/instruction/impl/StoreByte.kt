package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

class StoreByte : Instruction(0xAAu, "stosb") {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> "es:edi [${hex(processor.es.offset(processor.di.ex).toUInt())}], al"
		AddressingLength.R16 -> "es:di [${hex(processor.es.offset(processor.di.x).toUShort())}], al"
		else -> throw UnsupportedOperationException()
	}

	override fun handle(processor: IA32Processor) {
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				processor.computer.setMemoryAt(
					processor.es.offset(processor.di.ex),
					processor.a.tl
				)
				processor.di.ex++
			}

			AddressingLength.R16 -> {
				processor.computer.setMemoryAt(
					processor.es.offset(processor.di.x),
					processor.a.tl
				)
				processor.di.x++
			}

			else -> throw UnsupportedOperationException()
		}
	}
}