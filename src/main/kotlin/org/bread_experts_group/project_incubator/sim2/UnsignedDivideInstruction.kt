package org.bread_experts_group.project_incubator.sim2

sealed class UnsignedDivideInstruction : Instruction {
	class EAXModRm1632(val mod: Int, val rm: Int) : UnsignedDivideInstruction() {
		override fun execute(processor80386: Processor80386) {
			if (processor80386.address32) TODO("A32")
			else {
				val (sAddress, sRegister) = modRm16Addressing(processor80386, mod, rm)
				if (sAddress != null) TODO(" addr ")
				if (processor80386.operand32) {
					val divisor = (sRegister as Register32).qu
					val dividend = (processor80386.EDX.qu.toULong() shl 32) or processor80386.EAX.qu.toULong()
					val quotient = dividend / divisor
					if (quotient > 0xFFFFFFFFu) TODO("Divide error")
					processor80386.EDX.qu = (dividend % divisor).toUInt()
					processor80386.EAX.qu = quotient.toUInt()
					processor80386.logger.log(
						InstructionExecutionLogIdentifier(
							"DIV EAX,r/m32",
							operands = arrayOf(
								"EAX:EDX=${dividend}u",
								"${sRegister.label}=${divisor}u"
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