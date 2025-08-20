package org.bread_experts_group.computer.ia32.instruction.type

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.*
import org.bread_experts_group.computer.ia32.instruction.RegisterType

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
): ModRMDisassemblyResult =
	this.decoding.getModRMDisassembler(
		this.decoding.readFetch(),
		registerType,
		operandLength
	)

fun IA32Processor.rmB(
	registerType: RegisterType,
	operandLength: AddressingLength = this.operandSize
): Pair<ModRMResult, ModRMDisassemblyResult> {
	val saved = this.ip.rx
	val o1 = this.rm(registerType, operandLength)
	this.ip.rx = saved
	return o1 to this.rmD(registerType, operandLength)
}

fun IA32Processor.imm8(): UByte = this.decoding.readFetch()
fun IA32Processor.rel8(): Byte = this.decoding.readFetch().toByte()
fun IA32Processor.imm16(): UShort = this.decoding.readBinaryFetch(2).toUShort()
fun IA32Processor.rel16(): Short = this.decoding.readBinaryFetch(2).toShort()
fun IA32Processor.imm32(): UInt = this.decoding.readBinaryFetch(4).toUInt()
fun IA32Processor.rel32(): Int = this.decoding.readBinaryFetch(4).toInt()