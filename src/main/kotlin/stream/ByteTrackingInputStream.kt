package org.bread_experts_group.stream

import java.io.InputStream

class ByteTrackingInputStream(private val from: InputStream) : InputStream() {
	private val tracked = mutableListOf<Int>()
	override fun read(): Int = from.read().also {
		tracked.add(it)
	}

	fun dumpRead(): String {
		val dumped = tracked.joinToString("") { it.toUByte().toString(16).uppercase().padStart(2, '0') }
		tracked.clear()
		return dumped
	}
}