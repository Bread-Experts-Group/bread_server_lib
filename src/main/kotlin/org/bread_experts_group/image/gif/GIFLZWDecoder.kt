package org.bread_experts_group.image.gif

import java.io.ByteArrayOutputStream
import java.io.InputStream

class GIFLZWDecoder(
	private val input: InputStream,
	private val minimumLZW: Int
) {
	private val clearCode = 1 shl minimumLZW
	private val endCode = clearCode + 1

	fun decompress(): IntArray {
		var codeSize = minimumLZW + 1
		var dictSize = endCode + 1
		val dictionary = arrayOfNulls<ByteArray>(1 shl 13)
		var prevCode: Int? = null

		fun resetDictionary() {
			codeSize = minimumLZW + 1
			dictSize = endCode + 1
			for (i in 0 until clearCode) dictionary[i] = byteArrayOf(i.toByte())
			prevCode = null
		}
		resetDictionary()

		val outputStream = ByteArrayOutputStream(input.available())

		var buffer = 0
		var bitsInBuf = 0

		fun nextCode(): Int? {
			while (bitsInBuf < codeSize) {
				val next = input.read()
				if (next == -1) break
				buffer = buffer or (next shl bitsInBuf)
				bitsInBuf += 8
			}
			return if (bitsInBuf >= codeSize) {
				val mask = (1 shl codeSize) - 1
				val code = buffer and mask
				buffer = buffer ushr codeSize
				bitsInBuf -= codeSize
				code
			} else null
		}

		while (true) {
			val code = nextCode() ?: break
			when (code) {
				endCode -> break
				clearCode -> {
					resetDictionary()
					continue
				}
			}

			val entry: ByteArray = when {
				code < dictSize -> dictionary[code]!!
				prevCode != null -> {
					val prevEntry = dictionary[prevCode!!]!!
					prevEntry + prevEntry.first()
				}

				else -> throw IllegalArgumentException("unknown code $code")
			}

			outputStream.write(entry)
			if (prevCode != null) {
				val prevEntry = dictionary[prevCode!!]!!
				val newEntry = prevEntry + entry.first()
				dictionary[dictSize++] = newEntry
			}

			prevCode = code
			if (dictSize == (1 shl codeSize) && codeSize < 12) codeSize++
		}

		return outputStream.toByteArray().map { it.toInt() and 0xFF }.toIntArray()
	}
}