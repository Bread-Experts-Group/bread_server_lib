package org.bread_experts_group.stream

import java.io.IOException
import java.io.InputStream

open class FailQuickInputStream<S : InputStream>(val from: S) : LongInputStream() {
	class EndOfStream : IOException()

	override fun longAvailable(): ULong =
		if (from is LongInputStream) from.longAvailable()
		else from.available().toULong()

	override fun read(): Int = from.read().also { if (it == -1) throw EndOfStream() }
	override fun read(b: ByteArray, off: Int, len: Int): Int {
		val read = super.read(b, off, len)
		if (read == -1) throw EndOfStream()
		return read
	}

	override fun readAllBytes(): ByteArray = from.readAllBytes()
	override fun close() {
		from.close()
	}

	override fun mark(readlimit: Int) = from.mark(readlimit)
	override fun markSupported(): Boolean = from.markSupported()
	override fun reset() = from.reset()
}