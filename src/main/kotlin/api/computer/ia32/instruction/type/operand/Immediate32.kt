package org.bread_experts_group.api.computer.ia32.instruction.type.operand

import org.bread_experts_group.api.computer.BinaryUtil.read32
import org.bread_experts_group.api.computer.ia32.IA32Processor

interface Immediate32 {
	fun IA32Processor.imm32(): UInt = read32(this.decoding::readFetch)
	fun IA32Processor.rel32(): Int = read32(this.decoding::readFetch).toInt()
}