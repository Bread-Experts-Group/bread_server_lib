package org.bread_experts_group.stream

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.LinkedBlockingDeque

class ConsolidatedInputStream(blocking: Boolean) : InputStream() {
	val streams: LinkedBlockingDeque<InputStream> = LinkedBlockingDeque<InputStream>()

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

	private val retrieveMethod =
		if (blocking) LinkedBlockingDeque<InputStream>::takeFirst
		else LinkedBlockingDeque<InputStream>::pollFirst
	override fun read(): Int {
		val nextStream = retrieveMethod(streams)
		if (nextStream == null) return -1
		val read = nextStream.read()
		if (read == -1) return this.read()
		streams.putFirst(nextStream)
		return read
	}
}