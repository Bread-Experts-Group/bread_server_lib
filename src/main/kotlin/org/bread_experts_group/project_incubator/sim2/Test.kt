package org.bread_experts_group.project_incubator.sim2

import org.bread_experts_group.generic.io.reader.SegmentDirectDataProvisioner
import java.io.File
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

fun main() {
	val memoryBus = MemoryBus<UInt> { a, b -> a - b }
	val memoryArena = Arena.ofConfined()
	// 386 TEST
	val memory = memoryArena.allocate(0x100000)
	val rom = File("C:\\Users\\Adenosine3Phosphate\\Downloads\\test386.asm-master\\test386.bin")
		.readBytes()
	MemorySegment.copy(
		rom, 0,
		memory, ValueLayout.JAVA_BYTE, 0xF0000,
		0xFFFF
	)
	memoryBus.extents[0xFFFF0000u - 0xF0000u] = SegmentDirectDataProvisioner(memory).asUIntProvisioner()
	memoryBus.extents[0u] = SegmentDirectDataProvisioner(memory).asUIntProvisioner()
	// SeaBIOS
//	val memory = memoryArena.allocate(0x130000)
//	val rom = File("C:\\Users\\Adenosine3Phosphate\\Downloads\\bios.bin-1.7.0\\bios.bin-1.7.0")
//		.readBytes()
//	MemorySegment.copy(
//		rom, 0,
//		memory, ValueLayout.JAVA_BYTE, 0xF0000,
//		0x20000
//	)
//	memoryBus.extents[0xFFFFFFFFu - 0xF0000u - 0x20000u + 1u] = SegmentDirectDataProvisioner(memory).asUIntProvisioner()
//	memoryBus.extents[0u] = SegmentDirectDataProvisioner(memory).asUIntProvisioner()
	val proc = Processor80386(memoryBus, memoryBus)
	proc.logger.filter = { proc.CS.base == 983040u && proc.EIP.qu < 7456u }
	try {
		while (true) {
			proc.step()
		}
	} catch (e: Throwable) {
		proc.logger.flush()
		throw e
	}
}