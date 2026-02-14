package org.bread_experts_group.project_incubator.sim2

sealed class IntegerSubtractionInstruction : Instruction {
	class ModRM1632Imm8(val mod: Int, val rm: Int) : IntegerSubtractionInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) {
				if (processor80386.operand32) TODO("A32 O32")
				else TODO("A32 O16")
			} else {
				val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm)
				val imm8 = processor80386.instructionReadS8()
				if (processor80386.operand32) TODO("A16 O32")
				else {
					if (dAddress != null) TODO("A")
					else {
						val minuend = (dRegister as Register16).du
						val subtrahend = imm8.toUShort()
						val result = computeBorrow16(processor80386, minuend, subtrahend)
						computeZFPFSF16(processor80386, result)
						dRegister.du = result
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"SUB r/m16,imm8",
								arrayOf(minuend, subtrahend),
								arrayOf(
									"ZF=${processor80386.FLAGS.ZERO}",
									"SF=${processor80386.FLAGS.SIGN}",
									"PF=${processor80386.FLAGS.PARITY}",
									"CF=${processor80386.FLAGS.CARRY}",
									"AF=${processor80386.FLAGS.ADJUST}",
									"OF=${processor80386.FLAGS.OVERFLOW}",
								)
							)
						)
					}
				}
			}
		}
	}
}