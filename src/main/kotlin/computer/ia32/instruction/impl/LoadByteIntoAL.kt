package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType
import org.bread_experts_group.hex

class LoadByteIntoAL : Instruction(0xACu, "lodsb") {
	override fun operands(processor: IA32Processor): String {
		val segment = processor.segment ?: processor.ds
		return "al, ${segment.name}:si " +
				"[${hex(segment.tx)}:${hex(processor.si.tex)}] [${hex(segment.offset(processor.si))}]"
	}

	override fun handle(processor: IA32Processor) {
		val segment = processor.segment ?: processor.ds
		processor.a.tl = processor.computer.requestMemoryAt(segment.offset(processor.si.x))
		processor.si.x = (if (processor.flags.getFlag(FlagType.DIRECTION_FLAG)) ULong::minus else ULong::plus)(
			processor.si.x,
			1u
		)
	}
}