package org.bread_experts_group.stream

import java.io.DataInput
import java.io.EOFException
import java.io.InputStream

class DataInputProxyStream(private val from: DataInput) : InputStream() {
	override fun read(): Int = try {
		from.readByte().toUByte().toInt()
	} catch (_: EOFException) {
		throw FailQuickInputStream.EndOfStream()
	}

	override fun readNBytes(len: Int): ByteArray = try {
		val allocated = ByteArray(len)
		from.readFully(allocated)
		allocated
	} catch (_: EOFException) {
		throw FailQuickInputStream.EndOfStream()
	}
}