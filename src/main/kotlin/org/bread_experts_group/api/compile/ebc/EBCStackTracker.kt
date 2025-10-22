package org.bread_experts_group.api.compile.ebc

class EBCStackTracker(private val procedure: EBCProcedure) {
	private val stack: ArrayDeque<EBCCompilerStackType> = ArrayDeque()
	val last: EBCCompilerStackType
		get() = stack.last()

	fun expect32() {}
	fun expect64() {}
	fun expectNatural() {}

	fun POP32(operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?) {
		expect32()
		procedure.POP32(operand1, operand1Indirect, operand1Index)
	}

	fun POP64(operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?) {
		expect64()
		procedure.POP64(operand1, operand1Indirect, operand1Index)
	}

	fun POPn(operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?) {
		expectNatural()
		procedure.POPn(operand1, operand1Indirect, operand1Index)
	}

	fun PUSH32(operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?) {
		procedure.PUSH32(operand1, operand1Indirect, operand1Index)
		stack.addLast(EBCCompilerStackType.BIT_32)
	}

	fun PUSH64(operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?) {
		procedure.PUSH64(operand1, operand1Indirect, operand1Index)
		stack.addLast(EBCCompilerStackType.BIT_64)
	}

	fun PUSHn(operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?) {
		procedure.PUSHn(operand1, operand1Indirect, operand1Index)
		stack.addLast(EBCCompilerStackType.NATURAL)
	}
}