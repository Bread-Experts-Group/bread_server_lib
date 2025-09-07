package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction

class LogicalAND : InstructionCluster {
	class TwoOperand8Bit(
		opcode: UInt,
		d: () -> String,
		dc: Input2<UByte>
	) : LogicalCommon.TwoOperand8Bit(
		"and", UByte::and,
		opcode, d, dc
	)

	class TwoOperand(
		opcode: UInt,
		d16: () -> String,
		dc16: Input2<UShort>,
		d32: () -> String,
		dc32: Input2<UInt>
	) : LogicalCommon.TwoOperand(
		"and", UShort::and, UInt::and,
		opcode, d16, dc16, d32, dc32
	)

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		TwoOperand8Bit(
			0x20u,
			d8MR(processor), dc8MR(processor)
		),
		TwoOperand(
			0x21u,
			d16MR(processor), dc16MR(processor),
			d32MR(processor), dc32MR(processor)
		),
		TwoOperand8Bit(
			0x22u,
			d8RM(processor), dc8RM(processor)
		),
		TwoOperand(
			0x23u,
			d16RM(processor), dc16RM(processor),
			d32RM(processor), dc32RM(processor)
		),
		TwoOperand8Bit(
			0x24u,
			d8ALImm(processor), dc8ALImm(processor)
		),
		TwoOperand(
			0x25u,
			d16AXImm(processor), dc16AXImm(processor),
			d32EAXImm(processor), dc32EAXImm(processor)
		)
	)
}