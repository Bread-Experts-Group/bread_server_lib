package org.bread_experts_group.project_incubator.sim3a.hardware.nes

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.project_incubator.sim3a.aio.ArrayIO

class INESCartridge(
	val prgRom: ByteArray,
	val chrRom: ByteArray,
	val mapper: UInt
) {
	companion object {
		val INES_SIGNATURE = byteArrayOf(0x4E, 0x45, 0x53, 0x1A)
		fun decode(data: ArrayIO.ReadSequential<Int>): INESCartridge {
			if (!data.readArray(4).contentEquals(INES_SIGNATURE)) throw IllegalArgumentException()
			val prgRomSize = data.readUByte() * (16u * 1024u)
			val chrRomSize = data.readUByte() * (8u * 1024u)
			val flags6 = data.readUByte()
			val flags7 = data.readUByte()
			val flags8 = data.readUByte()
			val flags9 = data.readUByte()
			data.skip(6)
			val mapper = (flags6.toUInt() shr 4) or (flags7.toUInt() and 0b1111_0000u)
			return INESCartridge(
				data.readArray(prgRomSize.toInt()),
				data.readArray(chrRomSize.toInt()),
				mapper
			)
		}
	}

	override fun toString(): String = genericToString(this)
}