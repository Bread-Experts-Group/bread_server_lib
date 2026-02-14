package org.bread_experts_group.project_incubator.sim2

sealed class UnsignedMultiplyInstruction : Instruction {
	class EAXModRm1632(val mod: Int, val rm: Int) : UnsignedMultiplyInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) TODO("A32")
			else {
				val (sAddress, sRegister) = modRm16Addressing(processor80386, mod, rm)
				if (sAddress != null) TODO(" addr ")
				if (processor80386.operand32) {
					val result = processor80386.EAX.qu.toULong() * (sRegister as Register32).qu
					processor80386.EDX.qu = (result shr 32).toUInt()
					processor80386.EAX.qu = result.toUInt()
					processor80386.FLAGS.CARRY = processor80386.EDX.qu != 0u
					processor80386.FLAGS.OVERFLOW = processor80386.FLAGS.CARRY
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"MUL EAX,r/m32",
							operands = arrayOf(
								"EAX",
								sRegister.label
							),
							effects = arrayOf(
								"EDX=${processor80386.EDX.qu}u",
								"EAX=${processor80386.EAX.qu}u"
							),
							sideEffects = arrayOf(
								"CF=${processor80386.FLAGS.CARRY}",
								"OF=${processor80386.FLAGS.OVERFLOW}",
							)
						)
					)
				} else TODO("A16 O16")
			}
		}
	}
}