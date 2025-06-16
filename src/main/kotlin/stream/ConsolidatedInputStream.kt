package org.bread_experts_group.stream

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

class ConsolidatedInputStream : InputStream() {
	val streams: ArrayDeque<InputStream> = ArrayDeque()

	override fun available(): Int = streams.sumOf { it.available() }
	override fun readAllBytes(): ByteArray {
		val output = ByteArrayOutputStream()
		this.transferTo(output)
		return output.toByteArray()
	}

	override fun transferTo(out: OutputStream): Long {
		val transferred = streams.sumOf { it.transferTo(out) }
		streams.clear()
		return transferred
	}

	override fun read(): Int {
		val nextStream = streams.removeFirstOrNull()
		if (nextStream == null) return -1
		val read = nextStream.read()
		if (read == -1) return this.read()
		streams.addFirst(nextStream)
		return read
	}
}