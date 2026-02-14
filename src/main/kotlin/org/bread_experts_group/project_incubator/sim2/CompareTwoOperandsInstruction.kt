package org.bread_experts_group.project_incubator.sim2

sealed class CompareTwoOperandsInstruction : Instruction {
	object ALImm8 : CompareTwoOperandsInstruction() {
		override fun execute(processor80386: Processor80386) {
			val imm8 = processor80386.instructionReadU8K()
			val result = computeBorrow8(processor80386, processor80386.AL.bu, imm8)
			computeZFPFSF8(processor80386, result)
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"CMP AL,imm8",
					arrayOf(processor80386.AL.bu, imm8),
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

	object EAXImm1632 : CompareTwoOperandsInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val imm32 = processor80386.instructionReadU32K()
				val result = computeBorrow32(processor80386, processor80386.EAX.qu, imm32)
				computeZFPFSF32(processor80386, result)
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"CMP EAX,imm32",
						arrayOf(processor80386.EAX.qu, imm32),
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
			} else {
				val imm16 = processor80386.instructionReadU16K()
				val result = computeBorrow16(processor80386, processor80386.AX.du, imm16)
				computeZFPFSF16(processor80386, result)
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"CMP AX,imm16",
						arrayOf(processor80386.AX.du, imm16),
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

	class ModRM1632Imm1632(val mod: Int, val rm: Int) : CompareTwoOperandsInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) {
				if (processor80386.operand32) {
					TODO("A32 O32")
				} else {
					TODO("A32 O16")
				}
			} else {
				val (sAddress, sRegister) = modRm16Addressing(processor80386, mod, rm)
				if (processor80386.operand32) {
					val imm32 = processor80386.instructionReadU32K()
					val minuend = if (sAddress != null) processor80386.memorySource.readU32K(sAddress)
					else (sRegister as Register32).qu
					val result = computeBorrow32(processor80386, minuend, imm32)
					computeZFPFSF32(processor80386, result)
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"CMP r/m32,imm32",
							arrayOf(minuend, imm32),
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
				} else {
					val imm16 = processor80386.instructionReadU16K()
					val minuend = if (sAddress != null) processor80386.memorySource.readU16K(sAddress)
					else (sRegister as Register16).du
					val result = computeBorrow16(processor80386, minuend, imm16)
					computeZFPFSF16(processor80386, result)
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"CMP r/m16,imm16",
							arrayOf(minuend, imm16),
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

	object ModRMRegister1632 : CompareTwoOperandsInstruction() {
		override fun execute(processor80386: Processor80386) {
			val (mod, reg, rm) = decompose233(processor80386.instructionReadS8())
			if (processor80386.address32) {
				val (dAddress, dRegister) = modRm32Addressing(processor80386, mod, rm)
				if (processor80386.operand32) {
					val sRegister = rd(processor80386, reg)
					val minuend = if (dAddress != null) processor80386.memorySource.readU32K(dAddress)
					else (dRegister as Register32).qu
					val result = computeBorrow32(processor80386, minuend, sRegister.qu)
					computeZFPFSF32(processor80386, result)
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"CMP r/m32,imm32",
							arrayOf(
								minuend,
								sRegister.label to sRegister.qu
							),
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
				} else {
					val sRegister = rw(processor80386, reg)
					val minuend = if (dAddress != null) processor80386.memorySource.readU16K(dAddress)
					else (dRegister as Register16).du
					val result = computeBorrow16(processor80386, minuend, sRegister.du)
					computeZFPFSF16(processor80386, result)
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"CMP r/m16,imm16",
							arrayOf(
								minuend,
								sRegister.label to sRegister.du
							),
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
			} else {
				val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm)
				if (processor80386.operand32) {
					if (dAddress != null) TODO("Addr32")
					val sRegister = rd(processor80386, reg)
					val result = computeBorrow32(processor80386, (dRegister as Register32).qu, sRegister.qu)
					computeZFPFSF32(processor80386, result)
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"CMP r/m32,imm32",
							arrayOf(
								dRegister.label to dRegister.qu,
								sRegister.label to sRegister.qu
							),
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
				} else {
					val sRegister = rw(processor80386, reg)
					val minuend = if (dAddress != null) processor80386.memorySource.readU16K(dAddress)
					else (dRegister as Register16).du
					val result = computeBorrow16(processor80386, minuend, sRegister.du)
					computeZFPFSF16(processor80386, result)
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"CMP r/m16,imm16",
							arrayOf(
								minuend,
								sRegister.label to sRegister.du
							),
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

	class ModRM1632Imm8(val mod: Int, val rm: Int) : CompareTwoOperandsInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) {
				if (processor80386.operand32) {
					TODO("A32 O32")
				} else {
					TODO("A32 O16")
				}
			} else {
				val (sAddress, sRegister) = modRm16Addressing(processor80386, mod, rm)
				val imm8 = processor80386.instructionReadU8K()
				if (sAddress != null) TODO("!!!")
				if (processor80386.operand32) {
					val result = computeBorrow32(
						processor80386,
						(sRegister as Register32).qu,
						imm8.toByte().toUInt()
					)
					computeZFPFSF32(processor80386, result)
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"CMP r/m32,imm8",
							arrayOf(sRegister.label to sRegister.qu, imm8),
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
				} else {
					val result = computeBorrow16(
						processor80386,
						(sRegister as Register16).du,
						imm8.toByte().toUShort()
					)
					computeZFPFSF16(processor80386, result)
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"CMP r/m16,imm8",
							arrayOf(sRegister.label to sRegister.du, imm8),
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

	class ModRM8Imm8(val mod: Int, val rm: Int) : CompareTwoOperandsInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) {
				if (processor80386.operand32) {
					TODO("A32 O32")
				} else {
					TODO("A32 O16")
				}
			} else {
				val (sAddress, sRegister) = modRm16Addressing(processor80386, mod, rm, true)
				val imm8 = processor80386.instructionReadU8K()
				if (sAddress != null) TODO("!!!")
				if (processor80386.operand32) {
					TODO("A16 O32")
				} else {
					val result = computeBorrow8(
						processor80386,
						(sRegister as Register8).bu,
						imm8
					)
					computeZFPFSF8(processor80386, result)
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"CMP r/m8,imm8",
							arrayOf(sRegister.label to sRegister.bu, imm8),
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

	object ModRM8Reg8 : CompareTwoOperandsInstruction() {
		override fun execute(processor80386: Processor80386) {
			val (mod, reg, rm) = decompose233(processor80386.instructionReadS8())
			val sRegister = rb(processor80386, reg)
			if (processor80386.address32) {
				val (dAddress, dRegister) = modRm32Addressing(processor80386, mod, rm, true)
				val source = if (dAddress != null) processor80386.memorySource.readU8K(dAddress)
				else (dRegister as Register8).bu
				val result = computeBorrow8(
					processor80386,
					source,
					sRegister.bu
				)
				computeZFPFSF8(processor80386, result)
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"CMP r/m8,r8",
						arrayOf(source, sRegister.label to sRegister.bu),
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
			} else {
				val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm, true)
				if (dAddress != null) TODO("!!!")
				val result = computeBorrow8(
					processor80386,
					(dRegister as Register8).bu,
					sRegister.bu
				)
				computeZFPFSF8(processor80386, result)
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"CMP r/m8,r8",
						arrayOf(dRegister.label to dRegister.bu, sRegister.label to sRegister.bu),
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