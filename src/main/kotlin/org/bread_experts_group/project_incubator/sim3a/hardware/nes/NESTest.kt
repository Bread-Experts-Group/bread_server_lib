package org.bread_experts_group.project_incubator.sim3a.hardware.nes

import org.bread_experts_group.project_incubator.sim3a.aio.ArrayIO
import org.bread_experts_group.project_incubator.sim3a.aio.toArrayIO
import org.bread_experts_group.project_incubator.sim3a.hardware.HardwareW65C02S
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files
import kotlin.experimental.and
import kotlin.io.path.Path

interface Bit8Write<T> : ArrayIO.WriteExact<T> {
	override fun writeShort(at: T, v: Short) {
		throw NotImplementedError()
	}

	override fun writeInt(at: T, v: Int) {
		throw NotImplementedError()
	}

	override fun writeLong(at: T, v: Long) {
		throw NotImplementedError()
	}

	override var order: ByteOrder
		get() = throw NotImplementedError()
		set(value) = throw NotImplementedError()
}

fun main() {
	val ines = INESCartridge.decode(
		Files.readAllBytes(Path("C:\\Users\\Adenosine3Phosphate\\Desktop\\Donkey Kong (Japan).nes")).toArrayIO()
	)
	if (ines.mapper != 0u) TODO("!")
	if (ines.prgRom.size != 16384) TODO("!")
	if (ines.chrRom.size != 8192) TODO("!")

	var addr: UShort = 0u
	val vram = ByteBuffer.allocate(0x4000)
	val PPUDATA = object : Bit8Write<Int> {
		override fun writeByte(at: Int, v: Byte) {
			if (at != 0) throw IllegalArgumentException()
			vram.put(addr.toInt(), v)
			addr = (addr + 1u).toUShort() // TODO: read inc 0x2000
		}
	}
	val PPUADDR = object : Bit8Write<Int> {
		var configuring = false
		var stageAddr = 0
		override fun writeByte(at: Int, v: Byte) {
			if (!configuring) {
				stageAddr = (v and 0b0011_1111).toInt() shl 8
				configuring = true
			} else {
				addr = (stageAddr or (v.toInt() and 0xFF)).toUShort()
				configuring = false
			}
		}
	}
	val ppu = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val ppu2 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val ppu3 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val ppu4 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val ppu5 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val ppu6 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val controller = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).put(0, 0xFF.toByte())
	val controller2 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).put(0, 0xFF.toByte())
	val audio = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val audio2 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val audio3 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val audio4 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val audio5 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val audio6 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val audio7 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val audio8 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val audio9 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val audio10 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val audio11 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val audio12 = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN)
	val physicalMap = mapOf(
		0xC000u.toUShort() to ByteBuffer.wrap(ines.prgRom).order(ByteOrder.LITTLE_ENDIAN).toArrayIO(),
		0x8000u.toUShort() to ByteBuffer.wrap(ines.prgRom).order(ByteOrder.LITTLE_ENDIAN).toArrayIO(),
		0x4017u.toUShort() to controller2.toArrayIO(),
		0x4016u.toUShort() to controller.toArrayIO(),
		0x4015u.toUShort() to audio5.toArrayIO(),
		0x4014u.toUShort() to ppu6.toArrayIO(),
		0x400Bu.toUShort() to audio12.toArrayIO(),
		0x400Au.toUShort() to audio11.toArrayIO(),
		0x4008u.toUShort() to audio8.toArrayIO(),
		0x4007u.toUShort() to audio3.toArrayIO(),
		0x4006u.toUShort() to audio2.toArrayIO(),
		0x4005u.toUShort() to audio.toArrayIO(),
		0x4004u.toUShort() to audio4.toArrayIO(),
		0x4003u.toUShort() to audio10.toArrayIO(),
		0x4002u.toUShort() to audio9.toArrayIO(),
		0x4001u.toUShort() to audio7.toArrayIO(),
		0x4000u.toUShort() to audio6.toArrayIO(),
		0x2007u.toUShort() to PPUDATA,
		0x2006u.toUShort() to PPUADDR,
		0x2005u.toUShort() to ppu4.toArrayIO(),
		0x2003u.toUShort() to ppu5.toArrayIO(),
		0x2002u.toUShort() to ppu.toArrayIO(),
		0x2001u.toUShort() to ppu3.toArrayIO(), // 0b0110
		0x2000u.toUShort() to ppu2.toArrayIO(), // 0b1_0000
		0x0000u.toUShort() to ByteBuffer.allocate(0x2000).order(ByteOrder.LITTLE_ENDIAN).toArrayIO()
	)
	@Suppress("UNCHECKED_CAST") val memoryBus = object : ArrayIO.ReadExact<UShort>, ArrayIO.WriteExact<UShort> {
		private fun getLane(at: UShort): Pair<ArrayIO, UShort> {
			physicalMap.forEach { (address, bytes) ->
				if (at >= address) return bytes to (at - address).toUShort()
			}
			throw IllegalArgumentException("Addr ${at.toHexString()}")
		}

		override fun readByte(at: UShort): Byte {
			val (array, address) = getLane(at)
			return (array as ArrayIO.ReadExact<Int>).readByte(address.toInt())
		}

		override fun readShort(at: UShort): Short {
			val (array, address) = getLane(at)
			return (array as ArrayIO.ReadExact<Int>).readShort(address.toInt())
		}

		override fun readInt(at: UShort): Int {
			TODO("Not yet implemented")
		}

		override fun readLong(at: UShort): Long {
			TODO("Not yet implemented")
		}

		override fun readInto(at: UShort, array: ByteArray, offset: Int, size: Int) {
			TODO("Not yet implemented")
		}

		override var order: ByteOrder = ByteOrder.LITTLE_ENDIAN
		override fun writeByte(at: UShort, v: Byte) {
			val (array, address) = getLane(at)
			(array as ArrayIO.WriteExact<Int>).writeByte(address.toInt(), v)
		}

		override fun writeShort(at: UShort, v: Short) {
			val (array, address) = getLane(at)
			(array as ArrayIO.WriteExact<Int>).writeShort(address.toInt(), v)
		}

		override fun writeInt(at: UShort, v: Int) {
			TODO("Not yet implemented")
		}

		override fun writeLong(at: UShort, v: Long) {
			TODO("Not yet implemented")
		}
	}
	val framebuffer = IntArray(256 * 240)
	File("CODE").writeBytes(ines.prgRom)

	/*
	NTSC
	60  FRAMES PER SECOND (16.67 MS PER FRAME)
	262 SCANLINES (20 V BLANK) (113.33 CYCLES PER SCANLINE)
	256 X 224
	1.79 MHZ

	PAL
	50 FRAMES PER SECOND (20 MS PER FRAME)
	312 SCANLINES (70 V BLANK) (106.56 CYCLES PER SCANLINE)
	256 X 240
	1.66 MHZ
	 */

	/*
	When entering the V-Blank period, the PPU indicates this by setting bit 7 of I/O
	register $2002. This bit is reset when the CPU next reads from $2002.
	 */
	val nes = NesWindow(framebuffer)
	var scanline = 0
	var dot = 0

	val hw6502 = HardwareW65C02S(memoryBus, memoryBus)
	hw6502.reset()
	var a = 0
	try {
		while (true) {
			hw6502.step()
			a++
			repeat(18) {
				dot++
				if (dot == 256) {
					dot = 0
					scanline++
				}
				if (scanline == 261) {
					nes.render()
					scanline = 0
					hw6502.nmi = false
				}
				if (scanline > 240) {
					ppu.put(0, 0b1000_0000.toByte())
					if (ppu2.get(0).toInt() and 0b1000_0000 != 0) hw6502.nmi = true
				} else if (scanline < 240) {
					if (ppu3.get(0).toInt() and 0b0000_1000 != 0) {
						val ntiX = dot / 8
						val ntiY = scanline / 8
						val nm = when (ppu2.get(0).toInt() and 0b0000_0011) {
							0 -> 0x2000
							1 -> 0x2400
							2 -> 0x2800
							3 -> 0x2C00
							else -> throw IllegalArgumentException()
						}
						val tileIndex = vram.get(nm + (ntiY * 32) + ntiX).toInt() and 0xFF
						val pixelPosition = 7 - (dot % 8)
						val base = if (ppu2.get(0).toInt() and 0b0001_0000 != 0) 0x1000 else 0
						val pattern1 = ines.chrRom[base + (16 * tileIndex) + (scanline % 8)]
						val pattern2 = ines.chrRom[base + ((16 * tileIndex) + 8) + (scanline % 8)]
						val lit = (pattern1.toInt() shr pixelPosition) and 1 or
								(((pattern2.toInt() shr pixelPosition) and 1) shl 1)
						framebuffer[(scanline * 256) + dot] = when (lit) {
							0 -> 0x000000
							1 -> 0x555555
							2 -> 0xAAAAAA
							3 -> 0xFFFFFF
							else -> throw IllegalArgumentException()
						} or (0xFF shl 24)
					}
					ppu.put(0, 0)
				}
			}
		}
	} catch (e: Exception) {
		throw e
	} finally {
		println("$a instructions")
	}
}