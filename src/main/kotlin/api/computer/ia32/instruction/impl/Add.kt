package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.api.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.register.FlagsRegister.FlagType

fun addAuxCarryCheck(processor: IA32Processor, a: ULong, b: ULong) {
	processor.flags.setFlag(
		FlagType.AUXILIARY_CARRY_FLAG,
		(((a and 0xFu) + (b and 0xFu)) and 0x10u) != 0uL
	)
}

fun addAndSetFlagsAFCFOF8(processor: IA32Processor, a: UByte, b: UByte): UByte {
	addAuxCarryCheck(processor, a.toULong(), b.toULong())
	val added = a.toUShort() + b
	processor.flags.setFlag(FlagType.CARRY_FLAG, added > UByte.MAX_VALUE)
	processor.flags.setFlag(
		FlagType.OVERFLOW_FLAG,
		((a > 0u && b > 0u && added.toByte() < 0)) || ((a.toByte() < 0) && (b.toByte() < 0) && added.toByte() > 0)
	)
	return added.toUByte()
}

fun addAndSetFlagsAFCFOF16(processor: IA32Processor, a: UShort, b: UShort): UShort {
	addAuxCarryCheck(processor, a.toULong(), b.toULong())
	val added = a.toUInt() + b
	processor.flags.setFlag(FlagType.CARRY_FLAG, added > UShort.MAX_VALUE)
	processor.flags.setFlag(
		FlagType.OVERFLOW_FLAG,
		((a > 0u && b > 0u && added.toShort() < 0)) || ((a.toShort() < 0) && (b.toShort() < 0) && added.toShort() > 0)
	)
	return added.toUShort()
}

fun addAndSetFlagsAFCFOF32(processor: IA32Processor, a: UInt, b: UInt): UInt {
	addAuxCarryCheck(processor, a.toULong(), b.toULong())
	val added = a.toULong() + b
	processor.flags.setFlag(FlagType.CARRY_FLAG, added > UInt.MAX_VALUE)
	processor.flags.setFlag(
		FlagType.OVERFLOW_FLAG,
		((a > 0u && b > 0u && added.toInt() < 0)) || ((a.toInt() < 0) && (b.toInt() < 0) && added.toInt() > 0)
	)
	return added.toUInt()
}

class Add : InstructionCluster {
	class TwoOperand8Bit(
		opcode: UInt,
		val d: () -> String,
		val dc: Input2<UByte>
	) : Instruction(opcode, "add") {
		override fun operands(processor: IA32Processor): String = d()
		override fun handle(processor: IA32Processor) {
			val (dest, src) = dc()
			val result = addAndSetFlagsAFCFOF8(processor, dest.first(), src.first())
			dest.second(result)
			setFlagsSFZFPF8(processor, result)
		}
	}

	class TwoOperand(
		opcode: UInt,
		val d16: () -> String,
		val dc16: Input2<UShort>,
		val d32: () -> String,
		val dc32: Input2<UInt>
	) : Instruction(opcode, "add") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R16 -> d16()
			AddressingLength.R32 -> d32()
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor) {
			when (processor.operandSize) {
				AddressingLength.R16 -> {
					val (dest16, src16) = dc16()
					val result = addAndSetFlagsAFCFOF16(processor, dest16.first(), src16.first())
					dest16.second(result)
					setFlagsSFZFPF16(processor, result)
				}

				AddressingLength.R32 -> {
					val (dest32, src32) = dc32()
					val result = addAndSetFlagsAFCFOF32(processor, dest32.first(), src32.first())
					dest32.second(result)
					setFlagsSFZFPF32(processor, result)
				}

				else -> throw UnsupportedOperationException()
			}
		}
	}

	class TwoOperand8BitIncrement(
		opcode: UInt,
		val d: () -> String,
		val dc: Input1<UByte>
	) : Instruction(opcode, "inc") {
		override fun operands(processor: IA32Processor): String = d()
		override fun handle(processor: IA32Processor) {
			val saveCarry = processor.flags.getFlag(FlagType.CARRY_FLAG)
			val dest = dc()
			val result = addAndSetFlagsAFCFOF8(processor, dest.first(), 1u)
			dest.second(result)
			setFlagsSFZFPF8(processor, result)
			processor.flags.setFlag(FlagType.CARRY_FLAG, saveCarry)
		}
	}

	class TwoOperandIncrement(
		opcode: UInt,
		val d16: () -> String,
		val dc16: Input1<UShort>,
		val d32: () -> String,
		val dc32: Input1<UInt>
	) : Instruction(opcode, "inc") {
		override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
			AddressingLength.R16 -> d16()
			AddressingLength.R32 -> d32()
			else -> throw UnsupportedOperationException()
		}

		override fun handle(processor: IA32Processor) {
			val saveCarry = processor.flags.getFlag(FlagType.CARRY_FLAG)
			when (processor.operandSize) {
				AddressingLength.R16 -> {
					val dest16 = dc16()
					val result = addAndSetFlagsAFCFOF16(processor, dest16.first(), 1u)
					dest16.second(result)
					setFlagsSFZFPF16(processor, result)
				}

				AddressingLength.R32 -> {
					val dest32 = dc32()
					val result = addAndSetFlagsAFCFOF32(processor, dest32.first(), 1u)
					dest32.second(result)
					setFlagsSFZFPF32(processor, result)
				}

				else -> throw UnsupportedOperationException()
			}
			processor.flags.setFlag(FlagType.CARRY_FLAG, saveCarry)
		}
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		TwoOperand8Bit(
			0x00u,
			d8MR(processor), dc8MR(processor)
		),
		TwoOperand(
			0x01u,
			d16MR(processor), dc16MR(processor),
			d32MR(processor), dc32MR(processor)
		),
		TwoOperand8Bit(
			0x02u,
			d8RM(processor), dc8RM(processor)
		),
		TwoOperand(
			0x03u,
			d16RM(processor), dc16RM(processor),
			d32RM(processor), dc32RM(processor)
		),
		TwoOperand8Bit(
			0x04u,
			d8ALImm(processor), dc8ALImm(processor)
		),
		TwoOperand(
			0x05u,
			d16AXImm(processor), dc16AXImm(processor),
			d32EAXImm(processor), dc32EAXImm(processor)
		),
		TwoOperandIncrement(
			0x40u,
			d16O(processor, "ax"), dc16O(processor, processor.a::tx),
			d32O(processor, "eax"), dc32O(processor, processor.a::tex)
		),
		TwoOperandIncrement(
			0x41u,
			d16O(processor, "cx"), dc16O(processor, processor.c::tx),
			d32O(processor, "ecx"), dc32O(processor, processor.c::tex)
		),
		TwoOperandIncrement(
			0x42u,
			d16O(processor, "dx"), dc16O(processor, processor.d::tx),
			d32O(processor, "edx"), dc32O(processor, processor.d::tex)
		),
		TwoOperandIncrement(
			0x43u,
			d16O(processor, "bx"), dc16O(processor, processor.b::tx),
			d32O(processor, "ebx"), dc32O(processor, processor.b::tex)
		),
		TwoOperandIncrement(
			0x44u,
			d16O(processor, "sp"), dc16O(processor, processor.sp::tx),
			d32O(processor, "esp"), dc32O(processor, processor.sp::tex)
		),
		TwoOperandIncrement(
			0x45u,
			d16O(processor, "bp"), dc16O(processor, processor.bp::tx),
			d32O(processor, "ebp"), dc32O(processor, processor.bp::tex)
		),
		TwoOperandIncrement(
			0x46u,
			d16O(processor, "si"), dc16O(processor, processor.si::tx),
			d32O(processor, "esi"), dc32O(processor, processor.si::tex)
		),
		TwoOperandIncrement(
			0x47u,
			d16O(processor, "di"), dc16O(processor, processor.di::tx),
			d32O(processor, "edi"), dc32O(processor, processor.di::tex)
		)
	)
}