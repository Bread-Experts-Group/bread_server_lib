package org.bread_experts_group.api.computer.ia32.instruction.impl.group

import org.bread_experts_group.api.computer.ia32.instruction.DecodingUtil
import org.bread_experts_group.api.computer.ia32.instruction.RegisterType
import org.bread_experts_group.api.computer.ia32.instruction.impl.*
import org.bread_experts_group.api.computer.ia32.instruction.type.*
import org.bread_experts_group.hex

/**
 * TODO: Write an encompassing Javadoc for all instructions defined in this selector.
 * @author Miko Elbrecht
 * @since D0F0N0P0
 */
class Selector83h : InstructionSelector(0x83u) {
	val d16 = {
		val (m, _) = processor.rmD(RegisterType.GENERAL_PURPOSE, DecodingUtil.AddressingLength.R16)
		"$m, ${hex(processor.imm8())}"
	}
	val dc16 = {
		val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, DecodingUtil.AddressingLength.R16)
		val imm8 = processor.imm8().toUShort()
		(memRM::getRMs to memRM::setRMs) to ({ imm8 } to none16)
	}
	val d32 = {
		val (m, _) = processor.rmD(RegisterType.GENERAL_PURPOSE, DecodingUtil.AddressingLength.R32)
		"$m, ${hex(processor.imm8())}"
	}
	val dc32 = {
		val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, DecodingUtil.AddressingLength.R32)
		val imm8 = processor.imm8().toUInt()
		(memRM::getRMi to memRM::setRMi) to ({ imm8 } to none32)
	}

	override val instructions: Map<UInt, Instruction> by lazy {
		mapOf(
			0u to Add.TwoOperand(0u, d16, dc16, d32, dc32),
			1u to LogicalOR.TwoOperand(0u, d16, dc16, d32, dc32),
			2u to AddWithCarry.TwoOperand(0u, d16, dc16, d32, dc32),
			3u to SubtractWithBorrow.TwoOperand(0u, d16, dc16, d32, dc32),
			4u to LogicalAND.TwoOperand(0u, d16, dc16, d32, dc32),
			5u to Subtract.TwoOperand(0u, d16, dc16, d32, dc32),
			6u to LogicalXOR.TwoOperand(0u, d16, dc16, d32, dc32),
			7u to SubtractCompare.TwoOperand(0u, d16, dc16, d32, dc32)
		)
	}
}