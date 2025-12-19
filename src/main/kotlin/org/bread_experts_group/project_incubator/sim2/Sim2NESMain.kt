package org.bread_experts_group.project_incubator.sim2

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.open.FileIOReOpenFeatures
import org.bread_experts_group.io.reader.BSLReader
import org.bread_experts_group.io.reader.BSLReader.Companion.fileReadCheck
import org.bread_experts_group.io.reader.DirectDataProvisioner
import org.bread_experts_group.io.reader.SegmentDirectDataProvisioner
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.nio.ByteOrder

fun main() {
	val file = SystemProvider.get(SystemFeatures.GET_CURRENT_WORKING_PATH_DEVICE).device
		.get(SystemDeviceFeatures.PATH_APPEND)
		.append("C:\\Users\\Adenosine3Phosphate\\Downloads\\nes_test\\Super_mario_brothers.nes")
	val fileDevice = file.get(SystemDeviceFeatures.IO_DEVICE).open(
		FileIOReOpenFeatures.READ,
		FileIOReOpenFeatures.SHARE_READ
	).firstNotNullOf { it as? IODevice }
	val read = BSLReader(fileDevice.get(IODeviceFeatures.READ), fileReadCheck)
	read.order = ByteOrder.BIG_ENDIAN
	if (read.readU32k() != 0x4E45531Au) throw IllegalArgumentException("Not an iNES file")
	val prgRomSize = read.readU8i() * 16384
	val chrRomSize = read.readU8i() * 8192
	val flags = read.readU32l() or (read.readU8i() shl 32).toLong()
	read.skip(5)
	val nameTableArrangement = flags and 0b1 == 1L
	val batteryBackedPrgRam = flags and 0b10 != 0L
	if (batteryBackedPrgRam) TODO("PRG RAM")
	val trainerPresent = flags and 0b100 != 0L
	val altNameTable = flags and 0b1000 != 0L
	val mapperNo = ((flags ushr 12) and 0b1111) or ((flags and 0b11110000) ushr 4)
	if (mapperNo != 0L) TODO("Mapper $mapperNo")
	val vsUniSystem = flags and 0b1_00000000 != 0L
	val playChoice10 = flags and 0b10_00000000 != 0L
	val nes2Flags = (flags and 0b1100_00000000) ushr 10
	val prgRam = ((flags ushr 16) and 0xFF).let { if (it == 0L) 1 else it } * 8192
	val pal = (flags ushr 24) != 0L
	if (playChoice10) TODO("Play choice data")
	if (trainerPresent) TODO("?? trainer") //read.skip(512)
	val prgRom = read.readN(prgRomSize)
	val chrRom = read.readN(chrRomSize)
	val memoryBus = MemoryBus<UShort> { a, b -> (a - b).toUShort() }
	memoryBus.extents[0x2000u] = object : DirectDataProvisioner<UShort> {
		override fun readS8(at: UShort): Byte = when (at) {
			0x0002u.toUShort() -> 0b10000000.toByte()
			else -> throw IllegalStateException("Unknown PPU address @ 0x${at.toHexString()} (r)")
		}

		override fun readS16(at: UShort): Short = TODO("Not yet implemented")
		override fun readS32(at: UShort): Int = TODO("Not yet implemented")
		override fun readS64(at: UShort): Long = TODO("Not yet implemented")
		override fun readN(at: UShort, n: Int): ByteArray = TODO("Not yet implemented")
		override fun writeS8(at: UShort, b: Byte) = when (at) {
			0x0000u.toUShort() -> println("PPUCTRL 0x0 ${b.toString(2).padStart(8, '0')}")
			else -> throw IllegalStateException("Unknown PPU address @ 0x${at.toHexString()} (w ${b.toHexString()})")
		}

		override fun writeS16(at: UShort, s: Short) = TODO("Not yet implemented")
		override fun writeS32(at: UShort, i: Int) = TODO("Not yet implemented")
		override fun writeS64(at: UShort, l: Long) = TODO("Not yet implemented")
		override fun write(at: UShort, b: ByteArray, offset: Int, length: Int) = TODO("Not yet implemented")
		override fun fill(at: UShort, n: UShort, v: Byte) = TODO("Not yet implemented")
		override fun flush() = TODO("Not yet implemented")

		override var order: ByteOrder
			get() = TODO("Not yet implemented")
			set(value) {}
	}
	val memoryArena = Arena.ofConfined()
	val memory = memoryArena.allocate(0x8000)
	MemorySegment.copy(
		prgRom, 0,
		memory, ValueLayout.JAVA_BYTE, 0,
		0x8000
	)
	memoryBus.extents[0x8000u] = SegmentDirectDataProvisioner(memory).asUShortProvisioner()
	val processor = ProcessorMOS6502(memoryBus)
	while (true) {
		processor.step()
		Thread.sleep(1)
	}
}