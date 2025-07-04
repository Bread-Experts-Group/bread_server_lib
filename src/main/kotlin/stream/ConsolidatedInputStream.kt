package org.bread_experts_group.stream

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.LinkedBlockingDeque

class ConsolidatedInputStream(blocking: Boolean) : InputStream(), LongStream {
	val streams: LinkedBlockingDeque<InputStream> = LinkedBlockingDeque<InputStream>()

	override fun longAvailable(): Long = streams.sumOf {
		when (it) {
			is LongStream -> it.longAvailable()
			else -> it.available().toLong()
		}
	}

	override fun available(): Int = longAvailable().coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

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

	override fun read(b: ByteArray, off: Int, len: Int): Int {
		var read = 0
		while (read < len) {
			val nextStream = retrieveMethod(streams)
			if (nextStream == null) return if (read == 0) -1 else read
			val partialRead = nextStream.read(b, off + read, len - read)
			if (partialRead != -1) streams.putFirst(nextStream)
			read += partialRead
		}
		return 0
	}
}