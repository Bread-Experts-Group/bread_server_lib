package org.bread_experts_group.socket.failquick

import java.io.EOFException
import java.io.IOException
import java.io.InputStream

class FailQuickInputStream(private val from: InputStream) : InputStream() {
	override fun read(): Int {
		val next = try {
			from.read()
		} catch (_: IOException) {
			-1
		}
		if (next == -1) {
			this.close()
			throw EOFException()
		}
		return next
	}

	override fun close() {
		from.close()
		super.close()
	}
}