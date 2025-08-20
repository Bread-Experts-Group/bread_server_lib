package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.*
import org.bread_experts_group.hex
import kotlin.reflect.KMutableProperty0

typealias Input1<T> = () -> Pair<() -> T, (T) -> Unit>
typealias Input2<T> = () -> Pair<Pair<() -> T, (T) -> Unit>, Pair<() -> T, (T) -> Unit>>

val none8: (UByte) -> Unit = {}
val none16: (UShort) -> Unit = {}
val none32: (UInt) -> Unit = {}

fun d8MR(processor: IA32Processor, type: RegisterType = RegisterType.GENERAL_PURPOSE) = {
	val (m, r) = processor.rmD(type, AddressingLength.R8)
	"$m, $r"
}

fun d16MR(processor: IA32Processor, type: RegisterType = RegisterType.GENERAL_PURPOSE) = {
	val (m, r) = processor.rmD(type, AddressingLength.R16)
	"$m, $r"
}

fun d32MR(processor: IA32Processor, type: RegisterType = RegisterType.GENERAL_PURPOSE) = {
	val (m, r) = processor.rmD(type, AddressingLength.R32)
	"$m, $r"
}

fun dc8MR(processor: IA32Processor, type: RegisterType = RegisterType.GENERAL_PURPOSE): Input2<UByte> = {
	val (memRM, register) = processor.rm(type, AddressingLength.R8)
	(memRM::getRMb to memRM::setRMb) to ({ register.get().toUByte() } to { b: UByte -> register.set(b.toULong()) })
}

fun dc16MR(processor: IA32Processor, type: RegisterType = RegisterType.GENERAL_PURPOSE): Input2<UShort> = {
	val (memRM, register) = processor.rm(type, AddressingLength.R16)
	(memRM::getRMs to memRM::setRMs) to ({ register.get().toUShort() } to { s: UShort -> register.set(s.toULong()) })
}

fun dc32MR(processor: IA32Processor, type: RegisterType = RegisterType.GENERAL_PURPOSE): Input2<UInt> = {
	val (memRM, register) = processor.rm(type, AddressingLength.R32)
	(memRM::getRMi to memRM::setRMi) to ({ register.get().toUInt() } to { i: UInt -> register.set(i.toULong()) })
}

fun d16Oax(processor: IA32Processor, register: String) = { "ax, $register" }
fun d32Oeax(processor: IA32Processor, register: String) = { "eax, $register" }

fun dc16Oax(processor: IA32Processor, oRegister: KMutableProperty0<UShort>): Input2<UShort> = {
	({ processor.a.tx } to { s: UShort -> processor.a.tx = s }) to
			({ oRegister.get() } to { s: UShort -> oRegister.set(s) })
}

fun dc32Oeax(processor: IA32Processor, oRegister: KMutableProperty0<UInt>): Input2<UInt> = {
	({ processor.a.tex } to { i: UInt -> processor.a.tex = i }) to
			({ oRegister.get() } to { i: UInt -> oRegister.set(i) })
}

fun d16O(processor: IA32Processor, register: String) = { register }
fun d32O(processor: IA32Processor, register: String) = { register }
fun dc16O(processor: IA32Processor, register: KMutableProperty0<UShort>): Input1<UShort> = {
	({ register.get() } to { s: UShort -> register.set(s) })
}

fun dc32O(processor: IA32Processor, register: KMutableProperty0<UInt>): Input1<UInt> = {
	({ register.get() } to { i: UInt -> register.set(i) })
}

fun d8OI(processor: IA32Processor, register: String) = {
	"$register, ${hex(processor.imm8())}"
}

fun d16OI(processor: IA32Processor, register: String) = {
	"$register, ${hex(processor.imm16())}"
}

fun d32OI(processor: IA32Processor, register: String) = {
	"$register, ${hex(processor.imm32())}"
}

fun dc8OI(processor: IA32Processor, register: KMutableProperty0<UByte>): Input2<UByte> = {
	val imm8 = processor.imm8()
	({ register.get() } to { b: UByte -> register.set(b) }) to ({ imm8 } to none8)
}

fun dc16OI(processor: IA32Processor, register: KMutableProperty0<UShort>): Input2<UShort> = {
	val imm16 = processor.imm16()
	({ register.get() } to { s: UShort -> register.set(s) }) to ({ imm16 } to none16)
}

fun dc32OI(processor: IA32Processor, register: KMutableProperty0<UInt>): Input2<UInt> = {
	val imm32 = processor.imm32()
	({ register.get() } to { i: UInt -> register.set(i) }) to ({ imm32 } to none32)
}

fun moffsOffset(processor: IA32Processor) = when (processor.addressSize) {
	AddressingLength.R32 -> processor.imm32().toULong()
	AddressingLength.R16 -> processor.imm16().toULong()
	else -> throw UnsupportedOperationException()
}

fun d8TD(processor: IA32Processor) = {
	"${(processor.segment ?: processor.ds).hex(moffsOffset(processor))}, al"
}

fun d16TD(processor: IA32Processor) = {
	"${(processor.segment ?: processor.ds).hex(moffsOffset(processor))}, ax"
}

fun d32TD(processor: IA32Processor) = {
	"${(processor.segment ?: processor.ds).hex(moffsOffset(processor))}, eax"
}

fun dc8TD(processor: IA32Processor): Input2<UByte> = {
	val offset = (processor.segment ?: processor.ds).offset(moffsOffset(processor))
	({ processor.computer.requestMemoryAt(offset) } to
			{ b: UByte -> processor.computer.setMemoryAt(offset, b) }) to
			({ processor.a.tl } to { b: UByte -> processor.a.tl = b })
}

fun dc16TD(processor: IA32Processor): Input2<UShort> = {
	val offset = (processor.segment ?: processor.ds).offset(moffsOffset(processor))
	({ processor.computer.requestMemoryAt16(offset) } to
			{ s: UShort -> processor.computer.setMemoryAt16(offset, s) }) to
			({ processor.a.tx } to { s: UShort -> processor.a.tx = s })
}

fun dc32TD(processor: IA32Processor): Input2<UInt> = {
	val offset = (processor.segment ?: processor.ds).offset(moffsOffset(processor))
	({ processor.computer.requestMemoryAt32(offset) } to
			{ i: UInt -> processor.computer.setMemoryAt32(offset, i) }) to
			({ processor.a.tex } to { i: UInt -> processor.a.tex = i })
}

fun d8FD(processor: IA32Processor) = {
	"al, ${(processor.segment ?: processor.ds).hex(moffsOffset(processor))}"
}

fun d16FD(processor: IA32Processor) = {
	"ax, ${(processor.segment ?: processor.ds).hex(moffsOffset(processor))}"
}

fun d32FD(processor: IA32Processor) = {
	"eax, ${(processor.segment ?: processor.ds).hex(moffsOffset(processor))}"
}

fun dc8FD(processor: IA32Processor): Input2<UByte> = {
	val offset = (processor.segment ?: processor.ds).offset(moffsOffset(processor))
	({ processor.a.tl } to { b: UByte -> processor.a.tl = b }) to
			({ processor.computer.requestMemoryAt(offset) } to
					{ b: UByte -> processor.computer.setMemoryAt(offset, b) })
}

fun dc16FD(processor: IA32Processor): Input2<UShort> = {
	val offset = (processor.segment ?: processor.ds).offset(moffsOffset(processor))
	({ processor.a.tx } to { s: UShort -> processor.a.tx = s }) to
			({ processor.computer.requestMemoryAt16(offset) } to
					{ s: UShort -> processor.computer.setMemoryAt16(offset, s) })
}

fun dc32FD(processor: IA32Processor): Input2<UInt> = {
	val offset = (processor.segment ?: processor.ds).offset(moffsOffset(processor))
	({ processor.a.tex } to { i: UInt -> processor.a.tex = i }) to
			({ processor.computer.requestMemoryAt32(offset) } to
					{ i: UInt -> processor.computer.setMemoryAt32(offset, i) })
}

fun d8RM(processor: IA32Processor, type: RegisterType = RegisterType.GENERAL_PURPOSE) = {
	val (m, r) = processor.rmD(type, AddressingLength.R8)
	"$r, $m"
}

fun d16RM(processor: IA32Processor, type: RegisterType = RegisterType.GENERAL_PURPOSE) = {
	val (m, r) = processor.rmD(type, AddressingLength.R16)
	"$r, $m"
}

fun d32RM(processor: IA32Processor, type: RegisterType = RegisterType.GENERAL_PURPOSE) = {
	val (m, r) = processor.rmD(type, AddressingLength.R32)
	"$r, $m"
}

fun dc8RM(processor: IA32Processor, type: RegisterType = RegisterType.GENERAL_PURPOSE): Input2<UByte> = {
	val (memRM, register) = processor.rm(type, AddressingLength.R8)
	({ register.get().toUByte() } to { b: UByte -> register.set(b.toULong()) }) to (memRM::getRMb to memRM::setRMb)
}

fun dc16RM(processor: IA32Processor, type: RegisterType = RegisterType.GENERAL_PURPOSE): Input2<UShort> = {
	val (memRM, register) = processor.rm(type, AddressingLength.R16)
	({ register.get().toUShort() } to { s: UShort -> register.set(s.toULong()) }) to (memRM::getRMs to memRM::setRMs)
}

fun dc32RM(processor: IA32Processor, type: RegisterType = RegisterType.GENERAL_PURPOSE): Input2<UInt> = {
	val (memRM, register) = processor.rm(type, AddressingLength.R32)
	({ register.get().toUInt() } to { i: UInt -> register.set(i.toULong()) }) to (memRM::getRMi to memRM::setRMi)
}

fun d8ALImm(processor: IA32Processor) = { "al, ${hex(processor.imm8())}" }
fun d16AXImm(processor: IA32Processor) = { "ax, ${hex(processor.imm16())}" }
fun d32EAXImm(processor: IA32Processor) = { "eax, ${hex(processor.imm32())}" }

fun dc8ALImm(processor: IA32Processor): Input2<UByte> = {
	(processor.a::tl to { b: UByte -> processor.a.tl = b }) to (processor::imm8 to none8)
}

fun dc16AXImm(processor: IA32Processor): Input2<UShort> = {
	(processor.a::tx to { s: UShort -> processor.a.tx = s }) to (processor::imm16 to none16)
}

fun dc32EAXImm(processor: IA32Processor): Input2<UInt> = {
	(processor.a::tex to { i: UInt -> processor.a.tex = i }) to (processor::imm32 to none32)
}