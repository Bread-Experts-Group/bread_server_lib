package org.bread_experts_group.coder

object Base32 {
	private val BASE_32_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
		.withIndex().associate { it.value to it.index }

	fun decode(base32: String): ByteArray = buildList {
		var buffer = 0
		var bitsLeft = 0
		for (c in base32.uppercase()) {
			if (c == '=') break
			val value = BASE_32_SET[c] ?: throw DecodingException("Invalid Base32 character: $c")
			buffer = (buffer shl 5) or value
			bitsLeft += 5

			if (bitsLeft >= 8) {
				bitsLeft -= 8
				val byte = (buffer shr bitsLeft) and 0xFF
				add(byte.toByte())
			}
		}
	}.toByteArray()
}