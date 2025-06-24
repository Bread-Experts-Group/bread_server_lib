package org.bread_experts_group.computer.arm.v4.instruction

enum class InstructionOpcode(val code: UInt) {
	DATA_PROCESSING(0b00u),
	LOAD_STORE(0b01u),
	BRANCH(0b10u);

	companion object {
		val mapping = entries.associateBy(InstructionOpcode::code)
	}
}