package org.bread_experts_group.coder.format.huffman

import java.io.OutputStream

class BitOutputStream<T : OutputStream>(val to: T) : OutputStream() {
	override fun write(b: Int) = throw UnsupportedOperationException("Use writeBit() instead")

	var currentByte = 0
	var position = 7
	fun writeBit(bit: Boolean) {
		if (bit) currentByte = currentByte or (1 shl position)
		position--
		if (position == -1) this.flush()
	}

	override fun flush() {
		if (position != 7) {
			to.write(currentByte)
			currentByte = 0
			position = 7
		}
	}
}