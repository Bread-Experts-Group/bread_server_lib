package org.bread_experts_group.coder.format.parse

import java.io.OutputStream

class BitOutputStream<T : OutputStream>(val to: T) : OutputStream() {
	override fun write(b: Int): Nothing = throw UnsupportedOperationException("Use writeBit() instead")

	private var currentByte = 0
	var position: Int = 7
		private set

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