package org.bread_experts_group.stream

import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.LinkedBlockingDeque

class ConsolidatedInputStream : InputStream() {
	val streams = LinkedBlockingDeque<InputStream>()

	override fun available(): Int = streams.sumOf { it.available() }
	override fun transferTo(out: OutputStream): Long {
		val transferred = streams.sumOf { it.transferTo(out) }
		streams.clear()
		return transferred
	}

	override fun read(): Int {
		val nextStream = streams.takeFirst()
		val read = nextStream.read()
		if (read == -1) return this.read()
		streams.putFirst(nextStream)
		return read
	}
}