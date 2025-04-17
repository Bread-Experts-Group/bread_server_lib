package org.bread_experts_group.socket

import java.io.EOFException
import java.io.IOException
import java.io.InputStream

class FailQuickStream(private val from: InputStream) : InputStream() {
	override fun read(): Int {
		val next = try {
			from.read()
		} catch (_: IOException) {
			-1
		}
		if (next == -1) throw EOFException()
		return next
	}
}