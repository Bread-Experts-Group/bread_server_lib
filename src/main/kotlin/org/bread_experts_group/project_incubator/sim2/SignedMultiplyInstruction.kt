package org.bread_experts_group.project_incubator.sim2

sealed class SignedMultiplyInstruction : Instruction {
	class EAXModRm1632(val mod: Int, val rm: Int) : SignedMultiplyInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) TODO("A32")
			else {
				val (sAddress, sRegister) = modRm16Addressing(processor80386, mod, rm)
				if (sAddress != null) TODO("!A16")
				if (processor80386.operand32) {
					val value = (sRegister as Register32).q
					val oResult = processor80386.EAX.q * value
					val lResult = processor80386.EAX.q.toLong() * value
					processor80386.FLAGS.CARRY = oResult.toLong() != lResult
					processor80386.FLAGS.OVERFLOW = processor80386.FLAGS.CARRY
					processor80386.EDX.qu = (lResult.toULong() shr 32).toUInt()
					processor80386.EAX.qu = lResult.toULong().toUInt()
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"IMUL r/m32",
							operands = arrayOf(
								"EAX",
								sRegister.label
							),
							effects = arrayOf(
								"EDX=${processor80386.EDX.q}",
								"EAX=${processor80386.EAX.q}"
							),
							sideEffects = arrayOf(
								"CF=${processor80386.FLAGS.CARRY}",
								"OF=${processor80386.FLAGS.OVERFLOW}",
							)
						)
					)
				} else TODO("O16 $sRegister")
			}
		}
	}
}