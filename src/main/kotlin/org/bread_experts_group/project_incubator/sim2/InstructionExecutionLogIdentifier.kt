package org.bread_experts_group.project_incubator.sim2

import org.bread_experts_group.generic.logging.LogMessage

data class InstructionExecutionLogIdentifier(
	val mnemonic: String,
	val operands: Array<Any> = arrayOf(),
	val effects: Array<Any> = arrayOf(),
	val sideEffects: Array<Any> = arrayOf()
) : LogMessage