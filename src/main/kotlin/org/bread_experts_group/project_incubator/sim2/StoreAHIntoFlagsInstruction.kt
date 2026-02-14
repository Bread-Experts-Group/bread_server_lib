package org.bread_experts_group.project_incubator.sim2

object StoreAHIntoFlagsInstruction : Instruction {
	override fun execute(processor80386: Processor80386) {
		val bu = processor80386.AH.b.toUInt()
		processor80386.FLAGS.SIGN = bu and 0b10000000u != 0u
		processor80386.FLAGS.ZERO = bu and 0b01000000u != 0u
		processor80386.FLAGS.ADJUST = bu and 0b00010000u != 0u
		processor80386.FLAGS.PARITY = bu and 0b00000100u != 0u
		processor80386.FLAGS.CARRY = bu and 0b00000001u == 1u
		processor80386.logger.log(
			InstructionExecutionLogIdentifier(
				"SAHF",
				arrayOf(),
				arrayOf(
					"SF=${processor80386.FLAGS.SIGN}",
					"ZF=${processor80386.FLAGS.ZERO}",
					"AF=${processor80386.FLAGS.ADJUST}",
					"PF=${processor80386.FLAGS.PARITY}",
					"CF=${processor80386.FLAGS.CARRY}",
				)
			)
		)
	}
}