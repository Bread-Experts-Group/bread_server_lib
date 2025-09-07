package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.api.computer.ia32.instruction.type.imm16
import org.bread_experts_group.api.computer.ia32.instruction.type.imm8
import org.bread_experts_group.hex

class Enter : Instruction(0xC8u, "enter") {
	override fun operands(processor: IA32Processor): String = "${hex(processor.imm16())},${hex(processor.imm8())}"
	override fun handle(processor: IA32Processor) {
		val allocSize = processor.imm16()
		val nestingLevel = processor.imm8() % 32u
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				processor.push32(processor.bp.tex)
				val frameTemp = processor.sp.tex
				if (nestingLevel == 0u) {
					processor.bp.tex = frameTemp
					processor.sp.tex -= allocSize
				} else if (nestingLevel > 1u) {
					TODO("32 LRG")
				} else TODO("NC")
			}

			AddressingLength.R16 -> {
				processor.push16(processor.bp.tx)
				val frameTemp = processor.sp.tx
				if (nestingLevel == 0u) {
					processor.bp.tx = frameTemp
					processor.sp.x -= allocSize
				} else if (nestingLevel > 1u) {
					(1u..<nestingLevel).forEach { i ->
						when (processor.addressSize) {
							AddressingLength.R16 -> {
								processor.bp.x -= 2u
								processor.push16(
									processor.computer.getMemoryAt16(
										processor.ss.offset(processor.bp.x)
									)
								)
							}

							else -> throw UnsupportedOperationException()
						}
					}
				} else TODO("NC")
			}

			else -> throw UnsupportedOperationException()
		}
	}
}

/*
IF (NestingLevel > 1)
    THEN FOR i := 1 to (NestingLevel - 1)
        DO
            IF (OperandSize = 64)
                THEN
                    RBP := RBP - 8;
                    Push([RBP]); (* Quadword push *)
                ELSE IF OperandSize = 32
                    THEN
                        IF StackSize = 32
                            EBP := EBP - 4;
                            Push([EBP]); (* Doubleword push *)
                        ELSE (* StackSize = 16 *)
                            BP := BP - 4;
                            Push([BP]); (* Doubleword push *)
                        FI;
                    FI;
                FI;
    OD;
FI;
IF (OperandSize = 64) (* nestinglevel 1 *)
    THEN
        Push(FrameTemp); (* Quadword push and RSP decrements by 8 *)
    ELSE IF OperandSize = 32
        THEN
            Push(FrameTemp); FI; (* Doubleword push and (E)SP decrements by 4 *)
    ELSE (* OperandSize = 16 *)
            Push(FrameTemp); (* Word push and RSP|ESP|SP decrements by 2 *)
FI;
 */