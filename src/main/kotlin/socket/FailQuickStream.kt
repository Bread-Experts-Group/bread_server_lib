package org.bread_experts_group.socket

import java.io.EOFException
import java.io.InputStream

class FailQuickStream(private val from: InputStream) : InputStream() {
	override fun read(): Int = from.read().also {
		if (it == -1) throw EOFException()
	}
}