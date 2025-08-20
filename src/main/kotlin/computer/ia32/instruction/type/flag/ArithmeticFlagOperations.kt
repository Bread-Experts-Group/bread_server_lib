package org.bread_experts_group.computer.ia32.instruction.type.flag

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.register.FlagsRegister

interface ArithmeticFlagOperations {
	companion object {
		val HIGH_8 = (0b10000000).toUByte()
		val HIGH_16 = (0b10000000_00000000).toUShort()
		val HIGH_32 = (1u shl 31)
		val HIGH_64 = (1uL shl 63)
	}

	fun setFlagsForResult(processor: IA32Processor, v: UByte) {
		processor.flags.setFlag(FlagsRegister.FlagType.SIGN_FLAG, (v and HIGH_8) > 0u)
		processor.flags.setFlag(FlagsRegister.FlagType.ZERO_FLAG, v == 0u.toUByte())
		processor.flags.setFlag(FlagsRegister.FlagType.PARITY_FLAG, (v.countOneBits() % 2) == 0)
	}

	fun setFlagsForResult(processor: IA32Processor, v: UShort) {
		processor.flags.setFlag(FlagsRegister.FlagType.SIGN_FLAG, (v and HIGH_16) > 0u)
		processor.flags.setFlag(FlagsRegister.FlagType.ZERO_FLAG, v == 0u.toUShort())
		processor.flags.setFlag(FlagsRegister.FlagType.PARITY_FLAG, (v.countOneBits() % 2) == 0)
	}

	fun setFlagsForResult(processor: IA32Processor, v: UInt) {
		processor.flags.setFlag(FlagsRegister.FlagType.SIGN_FLAG, (v and HIGH_32) > 0u)
		processor.flags.setFlag(FlagsRegister.FlagType.ZERO_FLAG, v == 0u)
		processor.flags.setFlag(FlagsRegister.FlagType.PARITY_FLAG, (v.countOneBits() % 2) == 0)
	}

	fun setFlagsForResult(processor: IA32Processor, v: ULong) {
		TODO("L")
	}
}