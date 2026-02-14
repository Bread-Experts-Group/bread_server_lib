package org.bread_experts_group.project_incubator.sim2

sealed class LogicalExclusiveORInstruction : Instruction {
	sealed class ModRMRegister : LogicalExclusiveORInstruction() {
		object Bit1632 : ModRMRegister() {
			override fun execute(processor80386: Processor80386) {
				val (mod, reg, rm) = decompose233(processor80386.instructionReadS8())
				if (processor80386.operand32) {
					val sRegister = rd(processor80386, reg)
					if (processor80386.address32) {
						val (dAddress, dRegister) = modRm32Addressing(processor80386, mod, rm)
						TODO("A32 O32 $dAddress, $dRegister, $sRegister")
					} else {
						val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm)
						val dValue = if (dAddress != null) processor80386.memorySource.readU32K(dAddress)
						else (dRegister as Register32).qu
						val result = dValue xor sRegister.qu
						if (dAddress != null) processor80386.memorySink.writeU32K(dAddress, result)
						else (dRegister as Register32).qu = result
						processor80386.FLAGS.CARRY = false
						processor80386.FLAGS.OVERFLOW = false
						computeZFPFSF32(processor80386, result)
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"XOR r/m32,r32",
								arrayOf(dValue, sRegister.label),
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
				} else {
					val sRegister = rw(processor80386, reg)
					if (processor80386.address32) {
						val (dAddress, dRegister) = modRm32Addressing(processor80386, mod, rm)
						TODO("A32 O16 $dAddress, $dRegister, $sRegister")
					} else {
						val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm)
						if (dAddress != null) TODO("Choochoo")
						val dValue = (dRegister as Register16).du
						val result = dValue xor sRegister.du
						dRegister.du = result
						processor80386.FLAGS.CARRY = false
						processor80386.FLAGS.OVERFLOW = false
						computeZFPFSF16(processor80386, result)
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"XOR r/m16,r16",
								arrayOf(dValue, sRegister.label),
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
	}
}