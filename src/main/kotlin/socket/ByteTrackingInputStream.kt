package org.bread_experts_group.socket

import java.io.InputStream

class ByteTrackingInputStream(private val from: InputStream) : InputStream() {
	private val tracked = mutableListOf<Int>()
	override fun read(): Int = from.read().also {
		tracked.add(it)
	}

	fun dumpRead(): String {
		val dumped = tracked.joinToString(", ") {
			"0x${it.toUByte().toString(16).padStart(2, '0').uppercase()}"
		}
		tracked.clear()
		return dumped
	}
}