package org.bread_experts_group.project_incubator.sim2

sealed class LogicalANDInstruction : Instruction {
	class ModRM8Imm8(val mod: Int, val rm: Int) : LogicalANDInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) TODO("A32")
			else {
				val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm, true)
				val imm8 = processor80386.instructionReadU8K()
				if (dAddress != null) TODO("Addr")
				else {
					val result = (dRegister as Register8).bu and imm8
					dRegister.bu = result
					computeZFPFSF8(processor80386, result)
					processor80386.FLAGS.CARRY = false
					processor80386.FLAGS.OVERFLOW = false
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"AND r/m8,imm8",
							arrayOf(dRegister.label, imm8),
							arrayOf(
								"CF=false",
								"OF=false",
								"ZF=${processor80386.FLAGS.ZERO}",
								"SF=${processor80386.FLAGS.SIGN}",
								"PF=${processor80386.FLAGS.PARITY}"
							)
						)
					)
				}
			}
		}
	}

	object EAXImm1632 : LogicalANDInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val imm32 = processor80386.instructionReadU32K()
				val result = processor80386.EAX.qu and imm32
				processor80386.EAX.qu = result
				computeZFPFSF32(processor80386, result)
				processor80386.FLAGS.CARRY = false
				processor80386.FLAGS.OVERFLOW = false
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"AND EAX,imm32",
						arrayOf(imm32),
						arrayOf(
							"CF=false",
							"OF=false",
							"ZF=${processor80386.FLAGS.ZERO}",
							"SF=${processor80386.FLAGS.SIGN}",
							"PF=${processor80386.FLAGS.PARITY}"
						)
					)
				)
			} else TODO("O16")
		}
	}
}