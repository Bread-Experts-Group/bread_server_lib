package org.bread_experts_group.stream

import java.io.ByteArrayOutputStream
import java.io.OutputStream

interface Writable {
	fun write(stream: OutputStream)
	fun computeSize(): Long = asBytes().size.toLong()
	fun asBytes(): ByteArray = ByteArrayOutputStream().use {
		this.write(it)
		it.toByteArray()
	}
}