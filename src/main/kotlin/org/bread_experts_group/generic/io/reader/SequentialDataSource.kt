package org.bread_experts_group.generic.io.reader

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.time.Duration

interface SequentialDataSource {
	var order: ByteOrder
	var timeout: Duration

	fun readU8k(): Pair<UByte?, List<ReadingStatus>?> = readS8().let { it.first?.toUByte() to it.second }
	fun readU16k(): Pair<UShort?, List<ReadingStatus>?> = readS16().let { it.first?.toUShort() to it.second }
	fun readU32k(): Pair<UInt?, List<ReadingStatus>?> = readS32().let { it.first?.toUInt() to it.second }
	fun readU64k(): Pair<ULong?, List<ReadingStatus>?> = readS64().let { it.first?.toULong() to it.second }

	fun readU8i(): Pair<Int?, List<ReadingStatus>?> = readS8().let { it.first?.toInt()?.and(0xFF) to it.second }
	fun readU16i(): Pair<Int?, List<ReadingStatus>?> = readS16().let { it.first?.toInt()?.and(0xFFFF) to it.second }
	fun readU32l(): Pair<Long?, List<ReadingStatus>?> = readS32().let {
		it.first?.toLong()?.and(0xFFFFFFFF) to it.second
	}

	fun readS8(): Pair<Byte?, List<ReadingStatus>?>
	fun readS16(): Pair<Short?, List<ReadingStatus>?>
	fun readS32(): Pair<Int?, List<ReadingStatus>?>
	fun readS64(): Pair<Long?, List<ReadingStatus>?>

	fun readUTF8(): Pair<String?, List<ReadingStatus>?> {
		var codePoint = readU8i().let { it.first ?: return null to it.second }
		return when {
			(codePoint and 0b1_0000000) == 0 -> "${Char(codePoint)}"

			(codePoint and 0b111_00000) == 0b110_00000 -> {
				val b2 = readU8i().let { it.first ?: return null to it.second }
				codePoint =
					((codePoint and 0b111_00) shl 6) or ((((codePoint and 0b11) shl 2) or ((b2 ushr 4) and 0b11)) shl 4) or
							(b2 and 0b1111)
				if (codePoint < 0x80) TODO("Overlong encoding of 2 bytes")
				"${Char(codePoint)}"
			}

			(codePoint and 0b1111_0000) == 0b1110_0000 -> {
				val b2 = readU8i().let { it.first ?: return null to it.second }
				val b3 = readU8i().let { it.first ?: return null to it.second }
				codePoint = ((codePoint and 0b1111) shl 12) or (((b2 ushr 2) and 0b1111) shl 8) or
						((((b2 and 0b11) shl 2) or ((b3 ushr 4) and 0b11)) shl 4) or (b3 and 0b1111)
				if (codePoint < 0x800) TODO("Overlong encoding of 3 bytes")
				"${Char(codePoint)}"
			}

			(codePoint and 0b11111_000) == 0b11110_000 -> {
				val b2 = readU8i().let { it.first ?: return null to it.second }
				val b3 = readU8i().let { it.first ?: return null to it.second }
				val b4 = readU8i().let { it.first ?: return null to it.second }
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
		} to null
	}

	fun readN(n: Int): Triple<ByteArray, Int, List<ReadingStatus>?>
	fun read(into: ByteArray, offset: Int = 0, length: Int = into.size): Pair<Int, List<ReadingStatus>?>
	fun read(into: ByteBuffer): Pair<Int, List<ReadingStatus>?>
	fun skip(n: Long): Pair<Long, List<ReadingStatus>?>
}