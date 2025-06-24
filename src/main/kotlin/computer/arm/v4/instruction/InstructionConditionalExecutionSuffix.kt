package org.bread_experts_group.computer.arm.v4.instruction

enum class InstructionConditionalExecutionSuffix(val code: UInt) {
	EQ(0b0000u),
	NE(0b0001u),
	CS(0b0010u),
	CC(0b0011u),
	MI(0b0100u),
	PL(0b0101u),
	VS(0b0110u),
	VC(0b0111u),
	HI(0b1000u),
	LS(0b1001u),
	GE(0b1010u),
	LT(0b1011u),
	GT(0b1100u),
	LE(0b1101u),
	AL(0b1110u);

	companion object {
		val mapping = entries.associateBy(InstructionConditionalExecutionSuffix::code)
	}
}