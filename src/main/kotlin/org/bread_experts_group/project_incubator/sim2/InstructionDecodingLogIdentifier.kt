package org.bread_experts_group.project_incubator.sim2

import org.bread_experts_group.generic.logging.LogMessage

data class InstructionDecodingLogIdentifier(
	val csBase: UInt,
	val eip: UInt,
	val message: String
) : LogMessage