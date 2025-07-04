package org.bread_experts_group.stream

import java.io.IOException
import java.io.InputStream

open class FailQuickInputStream(private val from: InputStream) : InputStream(), LongStream {
	class EndOfStream : IOException()

	override fun longAvailable(): Long = if (from is LongStream) from.longAvailable() else from.available().toLong()
	override fun available(): Int = longAvailable().coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

	override fun read(): Int = from.read().also { if (it == -1) throw EndOfStream() }
	override fun read(b: ByteArray, off: Int, len: Int): Int {
		val read = super.read(b, off, len)
		if (read == -1) throw EndOfStream()
		return read
	}

	override fun close() {
		from.close()
	}
}