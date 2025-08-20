package org.bread_experts_group.computer.ia32.instruction.impl.group

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.impl.*
import org.bread_experts_group.computer.ia32.instruction.type.*
import org.bread_experts_group.hex

fun d8M1(processor: IA32Processor) = {
	"${processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R8).regMem}, 1"
}

fun d16M1(processor: IA32Processor) = {
	"${processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R16).regMem}, 1"
}

fun d32M1(processor: IA32Processor) = {
	"${processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R16).regMem}, 1"
}

fun dc8M1(processor: IA32Processor) = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R8)
	(memRM::getRMb to memRM::setRMb) to { 1 }
}

fun dc16M1(processor: IA32Processor) = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R16)
	(memRM::getRMs to memRM::setRMs) to { 1 }
}

fun dc32M1(processor: IA32Processor) = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R32)
	(memRM::getRMi to memRM::setRMi) to { 1 }
}

fun d8MC(processor: IA32Processor) = {
	"${processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R8).regMem}, ${processor.c.l}"
}

fun d16MC(processor: IA32Processor) = {
	"${processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R16).regMem}, ${processor.c.l}"
}

fun d32MC(processor: IA32Processor) = {
	"${processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R32).regMem}, ${processor.c.l}"
}

fun dc8MCi(processor: IA32Processor) = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R8)
	(memRM::getRMb to memRM::setRMb) to { processor.c.l.toInt() }
}

fun dc16MCi(processor: IA32Processor) = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R16)
	(memRM::getRMs to memRM::setRMs) to { processor.c.l.toInt() }
}

fun dc32MCi(processor: IA32Processor) = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R32)
	(memRM::getRMi to memRM::setRMi) to { processor.c.l.toInt() }
}

fun d8MI8(processor: IA32Processor) = {
	val (m, _) = processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R8)
	"$m, ${processor.imm8()}"
}

fun d16MI8(processor: IA32Processor) = {
	val (m, _) = processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R16)
	"$m, ${processor.imm8()}"
}

fun d32MI8(processor: IA32Processor) = {
	val (m, _) = processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R32)
	"$m, ${processor.imm8()}"
}

fun dc8MI8i(processor: IA32Processor) = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R8)
	val imm8 = processor.imm8().toInt()
	(memRM::getRMb to memRM::setRMb) to { imm8 }
}

fun dc16MI8i(processor: IA32Processor) = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R16)
	val imm8 = processor.imm8().toInt()
	(memRM::getRMs to memRM::setRMs) to { imm8 }
}

fun dc32MI8i(processor: IA32Processor) = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R32)
	val imm8 = processor.imm8().toInt()
	(memRM::getRMi to memRM::setRMi) to { imm8 }
}

fun d8M(processor: IA32Processor) = {
	processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R8).regMem
}

fun d16M(processor: IA32Processor) = {
	processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R16).regMem
}

fun d32M(processor: IA32Processor) = {
	processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R32).regMem
}

fun dc8M(processor: IA32Processor) = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R8)
	memRM::getRMb to memRM::setRMb
}

fun dc16M(processor: IA32Processor): Input1<UShort> = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R16)
	memRM::getRMs to memRM::setRMs
}

fun dc32M(processor: IA32Processor): Input1<UInt> = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R32)
	memRM::getRMi to memRM::setRMi
}

fun d8MI(processor: IA32Processor) = {
	val (m, _) = processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R8)
	"$m, ${hex(processor.imm8())}"
}

fun d16MI(processor: IA32Processor) = {
	val (m, _) = processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R16)
	"$m, ${hex(processor.imm16())}"
}

fun d32MI(processor: IA32Processor) = {
	val (m, _) = processor.rmD(RegisterType.GENERAL_PURPOSE, AddressingLength.R32)
	"$m, ${hex(processor.imm32())}"
}

fun dc8MI(processor: IA32Processor): Input2<UByte> = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R8)
	val imm8 = processor.imm8()
	(memRM::getRMb to memRM::setRMb) to ({ imm8 } to none8)
}

fun dc16MI(processor: IA32Processor): Input2<UShort> = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R16)
	val imm16 = processor.imm16()
	(memRM::getRMs to memRM::setRMs) to ({ imm16 } to none16)
}

fun dc32MI(processor: IA32Processor): Input2<UInt> = {
	val (memRM, _) = processor.rm(RegisterType.GENERAL_PURPOSE, AddressingLength.R32)
	val imm32 = processor.imm32()
	(memRM::getRMi to memRM::setRMi) to ({ imm32 } to none32)
}