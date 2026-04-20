package org.bread_experts_group.api.compile.ebc

import java.nio.ByteBuffer
import java.nio.ByteOrder

object EBCDisassembly {
	fun decodeIndex(n: Short): String {
		val n = n.toInt()
		var string = if ((n ushr 15) != 0) "-(" else "("
		val naturalBitCount = ((n ushr 12) and 0b111) * 2
		string += (n and ((1 shl naturalBitCount) - 1)).toString() + ", "
		string += ((n and ((1 shl 12) - 1)) ushr naturalBitCount).toString() + ")"
		return string
	}

	fun decodeIndex(n: Int): String {
		val n = n
		var string = if ((n ushr 31) != 0) "-(" else "("
		val naturalBitCount = ((n ushr 28) and 0b111) * 4
		string += (n and ((1 shl naturalBitCount) - 1)).toString() + ", "
		string += ((n and ((1 shl 28) - 1)) ushr naturalBitCount).toString() + ")"
		return string
	}

	fun decodeIndex(n: Long): String {
		val n = n
		var string = if ((n ushr 63) != 0L) "-(" else "("
		val naturalBitCount = (((n ushr 60) and 0b111) * 8).toInt()
		string += (n and ((1L shl naturalBitCount) - 1)).toString() + ", "
		string += ((n and ((1L shl 60) - 1)) ushr naturalBitCount).toString() + ")"
		return string
	}

	fun diassemble(code: ByteArray): String {
		val buffer = ByteBuffer.wrap(code).order(ByteOrder.LITTLE_ENDIAN)
		var output = ""
		while (buffer.hasRemaining()) {
			val byte1 = buffer.get().toInt() and 0xFF
			val byte2 = buffer.get().toInt() and 0xFF
			var description = ""
			when (val instruction = byte1 and 0b111111) {
				0x01 -> {
					val b64 = byte1 and 0b1000000 != 0
					description += "JMP${if (b64) "64" else "32"}${if (byte2 and 0b10000 != 0) "" else "a"}" +
							"${if (byte2 and 0b10000000 != 0) (if (byte2 and 0b1000000 != 0) "cs" else "cc") else ""} "
					if (b64) description += "x${buffer.getLong().toHexString()}"
					else {
						description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
						if (byte1 and 0b10000000 != 0) description += ' ' + (if (byte2 and 0b1000 != 0) decodeIndex(
							buffer.getInt()
						) else "x${buffer.getInt().toHexString()}")
					}
				}

				0x02 -> {
					description += "JMP8${
						if (byte1 and 0b10000000 != 0) (if (byte1 and 0b1000000 != 0) "cs" else "cc")
						else ""
					} ${byte2.toByte()}"
				}

				0x03 -> {
					val b64 = byte1 and 0b1000000 != 0
					description += "CALL${if (b64) "64" else "32"}${if (byte2 and 0b100000 != 0) "EX" else ""}" +
							"${if (byte2 and 0b10000 != 0) "" else "a"} "
					if (b64) description += "x${buffer.getLong().toHexString()}"
					else {
						description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
						if (byte1 and 0b10000000 != 0) description += ' ' + (if (byte2 and 0b1000 != 0) decodeIndex(
							buffer.getInt()
						) else "x${buffer.getInt().toHexString()}")
					}
				}

				0x04 -> description += "RET"

				0x05, 0x06, 0x07, 0x08, 0x09 -> {
					val b64 = byte1 and 0b1000000 != 0
					val kind = when (instruction) {
						0x05 -> "eq"
						0x06 -> "lte"
						0x07 -> "gte"
						0x08 -> "ulte"
						0x09 -> "ugte"
						else -> throw InternalError()
					}
					description += "CMP${if (b64) 64 else 32}$kind "
					description += EBCRegisters.entries[byte2 and 0b111]
					description += ", " + (if (byte2 and 0b10000000 != 0) "@" else "") +
							EBCRegisters.entries[(byte2 ushr 4) and 0b111]
				}

				0x2D, 0x2E, 0x2F, 0x30, 0x31 -> {
					val immediate32 = byte1 and 0b10000000 != 0
					val b64 = byte1 and 0b1000000 != 0
					val kind = when (instruction) {
						0x2D -> "eq"
						0x2E -> "lte"
						0x2F -> "gte"
						0x30 -> "ulte"
						0x31 -> "ugte"
						else -> throw InternalError()
					}
					description += "CMPI${if (b64) 64 else 32}$kind "
					description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
					if (byte2 and 0b10000 != 0) description += ' ' + decodeIndex(buffer.getShort())
					description += ", x" + if (immediate32) buffer.getInt().toHexString()
					else buffer.getShort().toHexString()
				}

				0x0C -> {
					val b64 = byte1 and 0b1000000 != 0
					description += "ADD${if (b64) 64 else 32} "
					description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
					description += ", " + (if (byte2 and 0b10000000 != 0) "@" else "") +
							EBCRegisters.entries[(byte2 ushr 4) and 0b111]
					if (byte1 and 0b10000000 != 0) description += ' ' + decodeIndex(buffer.getShort())
				}

				0x1D, 0x1E, 0x1F, 0x20, 0x21, 0x22, 0x23, 0x24, 0x28 -> {
					val size: EBCMoveTypes
					val indexSize: EBCMoveTypes
					if (instruction == 0x28) {
						size = EBCMoveTypes.BITS_64_QUADWORD
						indexSize = EBCMoveTypes.BITS_64_QUADWORD
					} else if (instruction >= 0x21) {
						size = when (instruction - 0x21) {
							0 -> EBCMoveTypes.BITS_8_BYTE
							1 -> EBCMoveTypes.BITS_16_WORD
							2 -> EBCMoveTypes.BITS_32_DOUBLEWORD
							3 -> EBCMoveTypes.BITS_64_QUADWORD
							else -> throw InternalError()
						}
						indexSize = EBCMoveTypes.BITS_32_DOUBLEWORD
					} else {
						size = when (instruction - 0x1D) {
							0 -> EBCMoveTypes.BITS_8_BYTE
							1 -> EBCMoveTypes.BITS_16_WORD
							2 -> EBCMoveTypes.BITS_32_DOUBLEWORD
							3 -> EBCMoveTypes.BITS_64_QUADWORD
							else -> throw InternalError()
						}
						indexSize = EBCMoveTypes.BITS_16_WORD
					}
					description += "MOV${size.letter}${indexSize.letter} "
					description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
					if (byte1 and 0b10000000 != 0) description += ' ' + when (indexSize) {
						EBCMoveTypes.BITS_64_QUADWORD -> decodeIndex(buffer.getLong())
						EBCMoveTypes.BITS_32_DOUBLEWORD -> decodeIndex(buffer.getInt())
						EBCMoveTypes.BITS_16_WORD -> decodeIndex(buffer.getShort())
						else -> throw InternalError()
					}
					description += ", " + (if (byte2 and 0b10000000 != 0) "@" else "") +
							EBCRegisters.entries[(byte2 ushr 4) and 0b111]
					if (byte1 and 0b1000000 != 0) {
						description += ' ' + when (indexSize) {
							EBCMoveTypes.BITS_64_QUADWORD -> decodeIndex(buffer.getLong())
							EBCMoveTypes.BITS_32_DOUBLEWORD -> decodeIndex(buffer.getInt())
							EBCMoveTypes.BITS_16_WORD -> decodeIndex(buffer.getShort())
							else -> throw InternalError()
						}
					}
				}

				0x2B, 0x2C -> {
					val b64 = byte1 and 0b1000000 != 0
					description += "${if (instruction == 0x2B) "PUSH" else "POP"}${if (b64) 64 else 32} "
					description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
					if (byte1 and 0b10000000 != 0) description += ' ' + decodeIndex(buffer.getShort())
				}

				0x32, 0x33 -> {
					description += "MOVn${if (instruction == 0x32) 'w' else 'd'} "
					description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
					if (byte1 and 0b10000000 != 0) description += ' ' + decodeIndex(buffer.getShort())
					description += ", " + (if (byte2 and 0b10000000 != 0) "@" else "") +
							EBCRegisters.entries[(byte2 ushr 4) and 0b111]
					if (byte1 and 0b1000000 != 0) description += ' ' + decodeIndex(buffer.getShort())
				}

				0x35, 0x36 -> {
					description += "${if (instruction == 0x35) "PUSH" else "POP"}n "
					description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
					if (byte1 and 0b10000000 != 0) description += ' ' + decodeIndex(buffer.getShort())
				}

				0x37 -> {
					val move: EBCMoveTypes = when ((byte2 ushr 4) and 0b11) {
						0 -> EBCMoveTypes.BITS_8_BYTE
						1 -> EBCMoveTypes.BITS_16_WORD
						2 -> EBCMoveTypes.BITS_32_DOUBLEWORD
						3 -> EBCMoveTypes.BITS_64_QUADWORD
						else -> throw InternalError()
					}
					val immediateSize: EBCMoveTypes = when (byte1 ushr 6) {
						1 -> EBCMoveTypes.BITS_16_WORD
						2 -> EBCMoveTypes.BITS_32_DOUBLEWORD
						3 -> EBCMoveTypes.BITS_64_QUADWORD
						else -> throw InternalError()
					}
					description += "MOVI${move.letter}${immediateSize.letter} "
					description += (if (byte2 and 0b1000 != 0) "@" else "") + EBCRegisters.entries[byte2 and 0b111]
					if (byte2 and 0b1000000 != 0) description += ' ' + decodeIndex(buffer.getShort())
					description += ", x" + when (immediateSize) {
						EBCMoveTypes.BITS_16_WORD -> buffer.getShort().toHexString()
						EBCMoveTypes.BITS_32_DOUBLEWORD -> buffer.getInt().toHexString()
						EBCMoveTypes.BITS_64_QUADWORD -> buffer.getLong().toHexString()
						else -> throw InternalError()
					}
				}

				else -> description += "??? $instruction ???"
			}
			output += description + "\n"
		}
		return output
	}
}