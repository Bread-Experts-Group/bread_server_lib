package org.bread_experts_group.api.computer.ia32.instruction.type

import org.bread_experts_group.api.computer.BinaryUtil.read16
import org.bread_experts_group.api.computer.BinaryUtil.read32
import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.DecodingUtil.*
import org.bread_experts_group.api.computer.ia32.instruction.RegisterType

fun IA32Processor.rm(
	registerType: RegisterType,
	operandLength: AddressingLength = this.operandSize
): ModRMResult = this.decoding.getModRM(
	this.decoding.readFetch(),
	registerType,
	operandLength
)

fun IA32Processor.rmD(
	registerType: RegisterType,
	operandLength: AddressingLength = this.operandSize
): ModRMDisassemblyResult = this.decoding.getModRMDisassembler(
	this.decoding.readFetch(),
	registerType,
	operandLength
)

fun IA32Processor.imm8(): UByte = this.decoding.readFetch()
fun IA32Processor.rel8(): Byte = this.decoding.readFetch().toByte()
fun IA32Processor.imm16(): UShort = read16(this.decoding::readFetch)
fun IA32Processor.imm32(): UInt = read32(this.decoding::readFetch)