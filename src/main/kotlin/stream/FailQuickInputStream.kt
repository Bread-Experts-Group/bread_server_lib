package org.bread_experts_group.stream

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

open class FailQuickInputStream(private val from: InputStream) : InputStream() {
	class EndOfStream : IOException()

	override fun available(): Int = from.available()
	override fun read(): Int = from.read().also { if (it == -1) throw EndOfStream() }
	override fun readAllBytes(): ByteArray = ByteArrayOutputStream().use {
		try {
			while (true) it.write(this.read())
		} catch (_: EndOfStream) {
		}
		it.toByteArray()
	}

	override fun close() {
		from.close()
	}
}