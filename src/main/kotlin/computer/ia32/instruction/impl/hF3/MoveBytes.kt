package org.bread_experts_group.computer.ia32.instruction.impl.hF3

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import kotlin.reflect.KMutableProperty0

class MoveBytes : Instruction(0xF3A4u, "rep movs") {
	override fun operands(processor: IA32Processor): String = when (processor.operandSize) {
		AddressingLength.R32 -> "es:[edi [${hex(processor.di.tex)}]], ds:[esi] -> " +
				"ds:[esi [${hex(processor.si.tex)}] + ecx [${hex(processor.c.tex)}]]"

		AddressingLength.R16 -> "es:[di [${hex(processor.di.tx)}]], ds:[si] -> " +
				"ds:[si [${hex(processor.si.tx)}] + cx [${hex(processor.c.tx)}]]"

		else -> throw UnsupportedOperationException()
	}

	private fun handle(
		processor: IA32Processor,
		c: KMutableProperty0<ULong>, si: KMutableProperty0<ULong>, di: KMutableProperty0<ULong>
	) {
		while (c.get() > 0u) {
			c.set(c.get() - 1u)
			processor.computer.setMemoryAt(
				processor.es.offset(si.get()),
				processor.computer.requestMemoryAt(processor.segment.offset(di.get()))
			)
			si.set(si.get() + 1u)
			di.set(di.get() + 1u)
		}
	}

	override fun handle(processor: IA32Processor): Unit = when (processor.operandSize) {
		AddressingLength.R32 -> this.handle(processor, processor.c::ex, processor.si::ex, processor.di::ex)
		AddressingLength.R16 -> this.handle(processor, processor.c::x, processor.si::x, processor.di::x)
		else -> throw UnsupportedOperationException()
	}
}