package org.bread_experts_group.io.reader

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.time.Duration

interface SequentialDataSource {
	var order: ByteOrder
	var timeout: Duration

	fun readU8k(): UByte = readS8().toUByte()
	fun readU16k(): UShort = readS16().toUShort()
	fun readU32k(): UInt = readS32().toUInt()
	fun readU64k(): ULong = readS64().toULong()

	fun readU8i(): Int = readS8().toInt() and 0xFF
	fun readU16i(): Int = readS16().toInt() and 0xFFFF
	fun readU32l(): Long = readS32().toLong() and 0xFFFFFFFF

	fun readS8(): Byte
	fun readS16(): Short
	fun readS32(): Int
	fun readS64(): Long

	fun readUTF8(): String {
		var codePoint = readU8i()
		return when {
			(codePoint and 0b1_0000000) == 0 -> "${Char(codePoint)}"

			(codePoint and 0b111_00000) == 0b110_00000 -> {
				val b2 = readU8i()
				codePoint =
					((codePoint and 0b111_00) shl 6) or ((((codePoint and 0b11) shl 2) or ((b2 ushr 4) and 0b11)) shl 4) or
							(b2 and 0b1111)
				if (codePoint < 0x80) TODO("Overlong encoding of 2 bytes")
				"${Char(codePoint)}"
			}

			(codePoint and 0b1111_0000) == 0b1110_0000 -> {
				val b2 = readU8i()
				val b3 = readU8i()
				codePoint = ((codePoint and 0b1111) shl 12) or (((b2 ushr 2) and 0b1111) shl 8) or
						((((b2 and 0b11) shl 2) or ((b3 ushr 4) and 0b11)) shl 4) or (b3 and 0b1111)
				if (codePoint < 0x800) TODO("Overlong encoding of 3 bytes")
				"${Char(codePoint)}"
			}

			(codePoint and 0b11111_000) == 0b11110_000 -> {
				val b2 = readU8i()
				val b3 = readU8i()
				val b4 = readU8i()
				codePoint =
					((codePoint and 0b100) shl 20) or (((codePoint and 0b11) shl 18) or ((b2 and 0b110000) shl 12)) or
							((b2 and 0b1111) shl 12) or ((b3 and 0b111100) shl 6) or
							(((b3 and 0b11) shl 6) or (b4 and 0b110000)) or (b4 and 0b1111)
				if (codePoint < 0x10000) TODO("Overlong encoding of 4 bytes")
				if (codePoint > 0x10FFFF) TODO("Bad UTF-8: Code point out of range")
				codePoint -= 0x10000
				"${Char(0xD800 + ((codePoint ushr 10) and 0x3FF))}${Char(0xDC00 + (codePoint and 0x3FF))}"
			}

			else -> TODO("Bad UTF-8: unrecognized format")
		}
	}

	fun readN(n: Int): ByteArray
	fun read(into: ByteBuffer)
	fun skip(n: Long)
}