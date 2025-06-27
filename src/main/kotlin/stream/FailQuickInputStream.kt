package org.bread_experts_group.stream

import java.io.IOException
import java.io.InputStream

open class FailQuickInputStream(private val from: InputStream) : InputStream() {
	class EndOfStream : IOException()

	override fun read(): Int = from.read().also { if (it == -1) throw EndOfStream() }
	override fun close() {
		from.close()
	}
}