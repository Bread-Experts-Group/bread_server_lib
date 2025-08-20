package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

class LogicalOR : InstructionCluster {
	class TwoOperand8Bit(
		opcode: UInt,
		d: () -> String,
		dc: Input2<UByte>,
	) : LogicalCommon.TwoOperand8Bit(
		"or", UByte::or,
		opcode, d, dc
	)

	class TwoOperand(
		opcode: UInt,
		d16: () -> String,
		dc16: Input2<UShort>,
		d32: () -> String,
		dc32: Input2<UInt>,
	) : LogicalCommon.TwoOperand(
		"or", UShort::or, UInt::or,
		opcode, d16, dc16, d32, dc32
	)

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		TwoOperand8Bit(
			0x08u,
			d8MR(processor), dc8MR(processor)
		),
		TwoOperand(
			0x09u,
			d16MR(processor), dc16MR(processor),
			d32MR(processor), dc32MR(processor)
		),
		TwoOperand8Bit(
			0x0Au,
			d8RM(processor), dc8RM(processor)
		),
		TwoOperand(
			0x0Bu,
			d16RM(processor), dc16RM(processor),
			d32RM(processor), dc32RM(processor)
		),
		TwoOperand8Bit(
			0x0Cu,
			d8ALImm(processor), dc8ALImm(processor)
		),
		TwoOperand(
			0x0Du,
			d16AXImm(processor), dc16AXImm(processor),
			d32EAXImm(processor), dc32EAXImm(processor)
		)
	)
}