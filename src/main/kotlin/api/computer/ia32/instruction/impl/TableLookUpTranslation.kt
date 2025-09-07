package org.bread_experts_group.api.computer.ia32.instruction.impl

import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.api.computer.ia32.instruction.type.Instruction

class TableLookUpTranslation : Instruction(0xD7u, "xlat") {
	override fun operands(processor: IA32Processor): String = ""
	override fun handle(processor: IA32Processor) {
		processor.a.tl = processor.computer.getMemoryAt(
			when (processor.operandSize) {
				AddressingLength.R32 -> (processor.segment ?: processor.ds).offset(processor.b.ex) + processor.a.l
				AddressingLength.R16 -> (processor.segment ?: processor.ds).offset(processor.b.x) + processor.a.l
				else -> throw UnsupportedOperationException()
			}
		)
	}
}