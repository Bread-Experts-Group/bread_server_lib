package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction

class LogicalCompare : InstructionCluster {
	class TwoOperand8Bit(
		opcode: UInt,
		d: () -> String,
		dc: Input2<UByte>,
	) : LogicalCommon.TwoOperand8Bit("test", UByte::and, opcode, d, dc)

	class TwoOperand(
		opcode: UInt,
		d16: () -> String,
		dc16: Input2<UShort>,
		d32: () -> String,
		dc32: Input2<UInt>,
	) : LogicalCommon.TwoOperand(
		"test", UShort::and, UInt::and,
		opcode,
		d16, dc16,
		d32, dc32
	)

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		TwoOperand8Bit(
			0x84u,
			d8MR(processor), dc8MR(processor)
		),
		TwoOperand(
			0x85u,
			d16MR(processor), dc16MR(processor),
			d32MR(processor), dc32MR(processor)
		),
		TwoOperand8Bit(
			0xA8u,
			d8ALImm(processor), dc8ALImm(processor)
		),
		TwoOperand(
			0xA9u,
			d16AXImm(processor), dc16AXImm(processor),
			d32EAXImm(processor), dc32EAXImm(processor)
		)
	)
}