package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType

fun moveDI(processor: IA32Processor, n: UInt) = when (processor.operandSize) {
	AddressingLength.R32 -> if (processor.flags.getFlag(FlagType.DIRECTION_FLAG)) processor.di.ex -= n
	else processor.di.ex += n

	AddressingLength.R16 -> if (processor.flags.getFlag(FlagType.DIRECTION_FLAG)) processor.di.x -= n
	else processor.di.x += n

	else -> throw UnsupportedOperationException()
}

fun moveSIDI(processor: IA32Processor, n: UInt) = when (processor.operandSize) {
	AddressingLength.R32 -> if (processor.flags.getFlag(FlagType.DIRECTION_FLAG)) {
		processor.di.ex -= n
		processor.si.ex -= n
	} else {
		processor.di.ex += n
		processor.si.ex += n
	}

	AddressingLength.R16 -> if (processor.flags.getFlag(FlagType.DIRECTION_FLAG)) {
		processor.di.x -= n
		processor.si.x -= n
	} else {
		processor.di.x += n
		processor.si.x += n
	}

	else -> throw UnsupportedOperationException()
}

fun dsSIOffset(processor: IA32Processor) = when (processor.operandSize) {
	AddressingLength.R32 -> processor.ds.offset(processor.si.ex)
	AddressingLength.R16 -> processor.ds.offset(processor.si.x)
	else -> throw UnsupportedOperationException()
}

fun esDIOffset(processor: IA32Processor) = when (processor.operandSize) {
	AddressingLength.R32 -> processor.es.offset(processor.di.ex)
	AddressingLength.R16 -> processor.es.offset(processor.di.x)
	else -> throw UnsupportedOperationException()
}