package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.computer.ia32.register.SegmentRegister
import org.bread_experts_group.hex

class LoadFarPointerFromMemoryDefinitions : InstructionCluster {
	class LoadFarPointerWithSegment(
		opcode: UInt,
		segmentN: Char,
		val segmentRegister: SegmentRegister
	) : Instruction(opcode, "l${segmentN}s"), ModRM {
		override fun operands(processor: IA32Processor): String = processor.rmD().let {
			"${segmentRegister.name} [${hex(segmentRegister.tx)}]:${it.register}, ${it.regMem}"
		}

		override fun handle(processor: IA32Processor) {
			val (memRm, register) = processor.rm()
			when (processor.operandSize) {
				AddressingLength.R16 -> {
					this.segmentRegister.tx = processor.computer.getMemoryAt16(memRm.memory!! + 2u)
					register.set(processor.computer.getMemoryAt32(memRm.memory).toULong())
				}

				else -> throw UnsupportedOperationException()
			}
		}

		override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
	}

	override fun getInstructions(processor: IA32Processor): List<Instruction> = listOf(
		LoadFarPointerWithSegment(0xC5u, 'd', processor.ds),
		LoadFarPointerWithSegment(0x0FB2u, 's', processor.ss),
		LoadFarPointerWithSegment(0xC4u, 'e', processor.es),
		LoadFarPointerWithSegment(0x0FB4u, 'f', processor.fs),
		LoadFarPointerWithSegment(0x0FB5u, 'g', processor.gs)
	)
}