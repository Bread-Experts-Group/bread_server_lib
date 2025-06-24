package org.bread_experts_group.computer.ia32.instruction.type

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.hex

abstract class Instruction(val opcode: UInt, open val mnemonic: String) {
	fun getDisassembly(processor: IA32Processor): String {
		val savedIP = processor.ip.rx
		val savedSP = processor.sp.rx
		val operands = this.operands(processor)
		processor.ip.rx = savedIP
		processor.sp.rx = savedSP
		return "${this.mnemonic} $operands"
	}

	abstract fun operands(processor: IA32Processor): String
	abstract fun handle(processor: IA32Processor)
	override fun toString(): String = "Instruction[${hex(opcode)} : $mnemonic]"
}