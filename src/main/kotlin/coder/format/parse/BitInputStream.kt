package org.bread_experts_group.coder.format.parse

import java.io.InputStream

open class BitInputStream(private val from: InputStream) : InputStream() {
	override fun read(): Nothing = throw UnsupportedOperationException("Use readBit() instead")

	private var maskOn: Int = -1
	private var maskPosition: Int = -1
	override fun available(): Int = (from.available() * 8) + (if (maskOn != -1) maskPosition else 0)

	fun nextBit(): Boolean {
		if (maskPosition == -1) {
			maskPosition = 7
			maskOn = from.read()
		}
		val updatedMask = maskPosition--
		return ((maskOn and (1 shl updatedMask)) shr updatedMask) != 0
	}
}