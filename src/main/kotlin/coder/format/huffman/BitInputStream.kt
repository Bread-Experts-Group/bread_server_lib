package org.bread_experts_group.coder.format.huffman

import java.io.InputStream

open class BitInputStream(private val from: InputStream) : InputStream() {
	override fun read(): Int = throw UnsupportedOperationException("Use readBit() instead")

	var maskOn: Int = 0
	var maskPosition: Int = -1
	override fun available(): Int = if (maskOn == -1) 0 else 1

	fun nextBit(): Boolean {
		if (maskPosition == -1) {
			maskPosition = 7
			maskOn = from.read()
		}
		val updatedMask = maskPosition--
		return ((maskOn and (1 shl updatedMask)) shr updatedMask) != 0
	}
}