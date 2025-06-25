package org.bread_experts_group.computer.arm.v4.instruction

enum class InstructionConditionalExecutionSuffix(val code: UInt, val assemblerName: String) {
	EQ(0b0000u, "eq"),
	NE(0b0001u, "ne"),
	CS(0b0010u, "cs"),
	CC(0b0011u, "cc"),
	MI(0b0100u, "mi"),
	PL(0b0101u, "pl"),
	VS(0b0110u, "vs"),
	VC(0b0111u, "vc"),
	HI(0b1000u, "hi"),
	LS(0b1001u, "ls"),
	GE(0b1010u, "ge"),
	LT(0b1011u, "lt"),
	GT(0b1100u, "gt"),
	LE(0b1101u, "le"),
	AL(0b1110u, "");

	companion object {
		val mapping = entries.associateBy(InstructionConditionalExecutionSuffix::code)
	}
}