package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType
import org.bread_experts_group.hex

class LoadWordIntoA : Instruction(0xADu, "lodsw/d") {
	override fun operands(processor: IA32Processor): String {
		val segment = processor.segment ?: processor.ds
		val first = when (processor.operandSize) {
			AddressingLength.R32 -> "eax [${hex(processor.a.tex)}], "
			AddressingLength.R16 -> "ax [${hex(processor.a.tx)}], "
			else -> throw UnsupportedOperationException()
		}
		return first + "[${hex(segment.tx)}:${hex(processor.si.tex)}] [${hex(segment.offset(processor.si))}]"
	}

	override fun handle(processor: IA32Processor) {
		val segment = processor.segment ?: processor.ds
		when (processor.operandSize) {
			AddressingLength.R32 -> TODO("lodsd")
			AddressingLength.R16 -> {
				processor.a.tx = processor.computer.requestMemoryAt16(segment.offset(processor.si.x))
				processor.si.x = (if (processor.flags.getFlag(FlagType.DIRECTION_FLAG)) ULong::minus else ULong::plus)(
					processor.si.x,
					2u
				)
			}

			else -> throw UnsupportedOperationException()
		}
	}
}