package org.bread_experts_group.project_incubator.sim2

/*
For programs executed in protected mode, the D-bit in executable-segment
descriptors determines the default attribute for both address size and
operand size. These default attributes apply to the execution of all
instructions in the segment. A value of zero in the D-bit sets the default
address size and operand size to 16 bits; a value of one, to 32 bits.

Programs that execute in real mode or virtual-8086 mode have 16-bit
addresses and operands by default.

/// 17.1.3 Address-Size Attribute for Stack
Instructions that use the stack implicitly (for example: POP EAX also have
a stack address-size attribute of either 16 or 32 bits. Instructions with a
stack address-size attribute of 16 use the 16-bit SP stack pointer register;
instructions with a stack address-size attribute of 32 bits use the 32-bit
ESP register to form the address of the top of the stack.

The stack address-size attribute is controlled by the B-bit of the
data-segment descriptor in the SS register. A value of zero in the B-bit
selects a stack address-size attribute of 16; a value of one selects a stack
address-size attribute of 32.
 */

sealed class JumpInstruction : Instruction {
	object Short : JumpInstruction() {
		override fun execute(processor80386: Processor80386) {
			val rel8 = processor80386.instructionReadS8()
			processor80386.EIP.qu += rel8.toUInt()
			if (!processor80386.operand32) processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
			processor80386.logger.log(
				InstructionExecutionLogIdentifier(
					"JMP rel8",
					arrayOf(rel8)
				)
			)
		}
	}

	object Near : JumpInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.operand32) {
				val rel32 = processor80386.instructionReadS32()
				processor80386.EIP.qu += rel32.toUInt()
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JMP rel32",
						arrayOf(rel32)
					)
				)
			} else {
				val rel16 = processor80386.instructionReadS16()
				processor80386.EIP.qu += rel16.toUInt()
				processor80386.EIP.qu = processor80386.EIP.qu and 0x0000FFFFu
				processor80386.logger.log(
					InstructionExecutionLogIdentifier(
						"JMP rel16",
						arrayOf(rel16)
					)
				)
			}
		}
	}

	sealed class Far : JumpInstruction() {
		object Ptr : Far() {
			override fun execute(processor80386: Processor80386) {
				if (
					!processor80386.CR0.PROTECTION_ENABLE ||
					(processor80386.CR0.PROTECTION_ENABLE && processor80386.EFLAGS.VIRTUAL_8086_MODE)
				) {
					if (processor80386.operand32) {
						val eip = processor80386.instructionReadS32()
						processor80386.CS.d = processor80386.instructionReadS16()
						processor80386.CS.base = processor80386.CS.d.toBitUInt() shl 4
						processor80386.EIP.q = eip
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"JMP ptr16:32",
								arrayOf(processor80386.CS.d, processor80386.EIP.q)
							)
						)
					} else {
						val ip = processor80386.instructionReadS16()
						processor80386.CS.d = processor80386.instructionReadS16()
						processor80386.CS.base = processor80386.CS.d.toBitUInt() shl 4
						processor80386.IP.d = ip
						processor80386.EIP.q = processor80386.EIP.q and 0x0000FFFF
						processor80386.logger.log(
							InstructionExecutionLogIdentifier(
								"JMP ptr16:16",
								arrayOf(processor80386.CS.d, processor80386.IP.d)
							)
						)
					}
				} else {
					if (processor80386.operand32) TODO("O32") else {
						val ip = processor80386.instructionReadS16()
						val cs = processor80386.instructionReadU16K()
						val data = processor80386.getSegmentDetails(cs)
						if (data.segment == null) TODO("#GP(0)")
						if (data.segment !is SegmentInformation.Code) TODO("#GP(selector)")

						TODO("!")
					}
				}
			}
		}
	}
}