package org.bread_experts_group.api.computer.ia32.instruction.type.operand

import org.bread_experts_group.api.computer.BinaryUtil.read16
import org.bread_experts_group.api.computer.ia32.IA32Processor

interface Immediate16 {
	fun IA32Processor.imm16(): UShort = read16(this.decoding::readFetch)
	fun IA32Processor.rel16(): Short = read16(this.decoding::readFetch).toShort()
}