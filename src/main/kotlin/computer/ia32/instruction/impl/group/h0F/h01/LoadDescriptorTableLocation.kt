package org.bread_experts_group.computer.ia32.instruction.impl.group.h0F.h01

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM
import org.bread_experts_group.computer.ia32.register.Register

/**
 * Loads a descriptor table into the base/limit registers of [baseR] and [limitR] respectively.
 * The instruction mnemonic takes the form of `l[n]dt r/m(16/32)`. |
 * Flags Modified: `none`
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class LoadDescriptorTableLocation(
	n: Char,
	val baseR: Register, val limitR: Register
) : Instruction(0u, "l${n}dt"), ModRM {
	override fun operands(processor: IA32Processor): String = processor.rmB().let { (rm, rmD) ->
		val addr = rm.memRM.memory!!
		return "${rmD.regMem} [${hex(processor.computer.getMemoryAt32(addr + 2u))} / " +
				"${hex(processor.computer.getMemoryAt16(addr))}]"
	}

	override fun handle(processor: IA32Processor) {
		val (memRM, _) = processor.rm()
		this.limitR.tx = processor.computer.getMemoryAt16(memRM.memory!!)
		this.baseR.tex = processor.computer.getMemoryAt32(memRM.memory + 2u)
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
}