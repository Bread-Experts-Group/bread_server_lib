package org.bread_experts_group.project_incubator.sim2

sealed class MoveInstruction : Instruction {
	/*
	TODO:
		Interrupt 13 if any part of the operand would lie outside of the effective
		address space from 0 to 0FFFFH

	TODO:
		#GP(0) if the destination is in a nonwritable segment; #GP(0) for an
		illegal memory operand effective address in the CS, DS, ES, FS, or GS
		segments; #SS(0) for an illegal address in the SS segment; #PF(fault-code)
		for a page fault
	 */
	sealed class ImmediateToRegister : MoveInstruction() {
		class Bit8(private val register: Int) : ImmediateToRegister() {
			override fun execute(processor80386: Processor80386) {
				val imm8 = processor80386.instructionReadS8()
				val register = rb(processor80386, register)
				register.b = imm8
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"MOV reg8,imm8",
						arrayOf(register.label, imm8)
					)
				)
			}
		}


		class Bit1632(private val register: Int) : ImmediateToRegister() {
			override fun execute(processor80386: Processor80386) {
				if (processor80386.operand32) {
					val imm32 = processor80386.instructionReadS32()
					val register = rd(processor80386, register)
					register.q = imm32
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"MOV reg32,imm32",
							arrayOf(register.label, imm32)
						)
					)
				} else {
					val imm16 = processor80386.instructionReadS16()
					val register = rw(processor80386, register)
					register.d = imm16
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"MOV reg16,imm16",
							arrayOf(register.label, imm16)
						)
					)
				}
			}
		}
	}

	sealed class RegisterToModRm : MoveInstruction() {
		object Bit1632 : RegisterToModRm() {
			override fun execute(processor80386: Processor80386) {
				val (mod, reg, rm) = decompose233(processor80386.instructionReadS8())
				if (processor80386.address32) {
					val (dAddress, _) = modRm32Addressing(processor80386, mod, rm)
					if (processor80386.operand32) {
						if (dAddress != null) {
							val sRegister = rd(processor80386, reg)
							processor80386.memorySink.writeU32K(dAddress, sRegister.qu)
							processor80386.logger.log(
								InstructionExecutionLogIdentifier(
									"MOV r/m32,r32",
									arrayOf(dAddress, sRegister.label)
								)
							)
						} else TODO("reg")
					} else {
						if (dAddress != null) {
							val sRegister = rw(processor80386, reg)
							processor80386.memorySink.writeU16K(dAddress, sRegister.du)
							processor80386.logger.log(
								InstructionExecutionLogIdentifier(
									"MOV r/m16,r16",
									arrayOf(dAddress, sRegister.label)
								)
							)
						} else TODO("reg")
					}
				} else {
					val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm)
					if (dAddress != null) TODO("Addr")
					if (processor80386.operand32) {
						val sRegister = rd(processor80386, reg)
						(dRegister as Register32).q = sRegister.q
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"MOV r/m32,r32",
								arrayOf(dRegister.label, sRegister.label)
							)
						)
					} else {
						val sRegister = rw(processor80386, reg)
						(dRegister as Register16).d = sRegister.d
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"MOV r/m16,r16",
								arrayOf(dRegister.label, sRegister.label)
							)
						)
					}
				}
			}
		}


		object Bit8 : RegisterToModRm() {
			override fun execute(processor80386: Processor80386) {
				val (mod, reg, rm) = decompose233(processor80386.instructionReadS8())
				val sRegister = rb(processor80386, reg)
				if (processor80386.address32) {
					val (dAddress, dRegister) = modRm32Addressing(processor80386, mod, rm, true)
					if (dAddress != null) {
						processor80386.memorySink.writeU8K(dAddress, sRegister.bu)
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"MOV r/m8,r8",
								arrayOf(dAddress, sRegister.label)
							)
						)
					} else {
						(dRegister as Register8).b = sRegister.b
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"MOV r/m8,r8",
								arrayOf(dRegister.label, sRegister.label)
							)
						)
					}
				} else {
					TODO("A16")
				}
			}
		}
	}

	sealed class ImmediateToModRm : MoveInstruction() {
		object Bit1632 : ImmediateToModRm() {
			override fun execute(processor80386: Processor80386) {
				if (processor80386.CR0.PROTECTION_ENABLE) TODO("Protected")
				val (mod, _, rm) = decompose233(processor80386.instructionReadS8())
				if (processor80386.operand32) {
					if (processor80386.address32) {
						val (sAddress, sRegister) = modRm32Addressing(processor80386, mod, rm)
						TODO("A32 O32 $sAddress, $sRegister")
					} else {
						val (sAddress, sRegister) = modRm16Addressing(processor80386, mod, rm)
						val imm32 = processor80386.instructionReadU32K()
						if (sRegister != null) TODO("Register")
						processor80386.memorySink.writeU32K(sAddress!!, imm32)
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"MOV r/m32,imm32",
								arrayOf(sAddress, imm32)
							)
						)
					}
				} else {
					if (processor80386.address32) {
						val (sAddress, sRegister) = modRm32Addressing(processor80386, mod, rm)
						val imm16 = processor80386.instructionReadU16K()
						if (sRegister != null) TODO("Register")
						processor80386.memorySink.writeU16K(sAddress!!, imm16)
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"MOV r/m16,imm16",
								arrayOf(sAddress, imm16)
							)
						)
					} else {
						val (sAddress, sRegister) = modRm16Addressing(processor80386, mod, rm)
						val imm16 = processor80386.instructionReadU16K()
						if (sRegister != null) TODO("Register")
						processor80386.memorySink.writeU16K(sAddress!!.toUInt(), imm16)
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"MOV r/m16,imm16",
								arrayOf(sAddress, imm16)
							)
						)
					}
				}
			}
		}
	}

	object ToSegment : MoveInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.CR0.PROTECTION_ENABLE) TODO("Protected")
			val (mod, reg, rm) = decompose233(processor80386.instructionReadS8())
			val dRegister = sReg(processor80386, reg)
			if (processor80386.operand32) {
				if (processor80386.address32) {
					val (sAddress, sRegister) = modRm32Addressing(processor80386, mod, rm)
					TODO("A32 O32 $sAddress, $sRegister, $dRegister")
				} else {
					val (sAddress, sRegister) = modRm16Addressing(processor80386, mod, rm)
					TODO("A16 O32 $sAddress, $sRegister, $dRegister")
				}
			} else {
				if (processor80386.address32) {
					val (sAddress, sRegister) = modRm32Addressing(processor80386, mod, rm)
					TODO("A32 O16 $sAddress, $sRegister, $dRegister")
				} else {
					val (sAddress, sRegister) = modRm16Addressing(processor80386, mod, rm)
					val datum = if (sAddress != null) processor80386.memorySource.readS16(sAddress)
					else (sRegister as Register16).d

					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"MOV Sreg,r/m16",
							arrayOf(dRegister.label, datum),
							arrayOf()
						)
					)

					if (dRegister == processor80386.CS) {
						processor80386.interrupt = 6
						return
					}

					dRegister.d = datum
					dRegister.base = dRegister.d.toUInt() shl 4
				}
			}
		}
	}

	object FromSegment : MoveInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.CR0.PROTECTION_ENABLE) TODO("Protected")
			val (mod, reg, rm) = decompose233(processor80386.instructionReadS8())
			val sRegister = sReg(processor80386, reg)
			if (processor80386.operand32) {
				if (processor80386.address32) {
					val (dAddress, dRegister) = modRm32Addressing(processor80386, mod, rm)
					TODO("A32 O32 $dAddress, $dRegister, $sRegister")
				} else {
					val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm)
					if (dAddress != null) TODO("A16 O32 $dAddress, $sRegister")
					(dRegister as Register32).d = sRegister.d

					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"MOV r/m16,Sreg",
							arrayOf(dRegister.label, sRegister.label),
							arrayOf()
						)
					)
				}
			} else {
				if (processor80386.address32) {
					val (dAddress, dRegister) = modRm32Addressing(processor80386, mod, rm)
					TODO("A32 O16 $dAddress, $dRegister, $sRegister")
				} else {
					val (dAddress, dRegister) = modRm16Addressing(processor80386, mod, rm)
					if (dAddress != null) {
						processor80386.memorySink.writeS16(dAddress, sRegister.d)

						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"MOV r/m16,Sreg",
								arrayOf(dAddress, sRegister.label),
								arrayOf()
							)
						)
					} else {
						(dRegister as Register16).d = sRegister.d

						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"MOV r/m16,Sreg",
								arrayOf(dRegister.label, sRegister.label),
								arrayOf()
							)
						)
					}
				}
			}
		}
	}

	object ControlRegisterToRegister : MoveInstruction() {
		override fun execute(processor80386: Processor80386) {
			val (_, reg, rm) = decompose233(processor80386.instructionReadS8())
			val dRegister = rd(processor80386, rm)
			val sRegister = rc(processor80386, reg)
			dRegister.q = sRegister.q
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"MOV r32,CR",
					arrayOf(dRegister.label, sRegister.label),
					arrayOf()
				)
			)
		}
	}

	object RegisterToControlRegister : MoveInstruction() {
		override fun execute(processor80386: Processor80386) {
			val (_, reg, rm) = decompose233(processor80386.instructionReadS8())
			val sRegister = rd(processor80386, rm)
			val dRegister = rc(processor80386, reg)
			dRegister.q = sRegister.q
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"MOV CR,r32",
					arrayOf(dRegister.label, sRegister.label),
					arrayOf()
				)
			)
		}
	}
}