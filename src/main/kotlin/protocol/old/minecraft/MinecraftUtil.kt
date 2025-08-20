package org.bread_experts_group.protocol.old.minecraft

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.CharsetEncoder

fun varSizeOfNl(n: Long): Int {
	var remainder = n ushr 7
	var size = 1
	while (remainder != 0L) {
		remainder = remainder ushr 7
		size++
	}
	return size
}

fun varSizeOfNi(n: Int): Int = varSizeOfNl(n.toLong() and 0xFFFFFFFF)

fun varNl(n: Long): ByteArray {
	val out = ByteArrayOutputStream()
	var remainder = n
	do {
		val filter = remainder and 0b01111111
		out.write(
			(if (remainder > 0b01111111) filter or 0b10000000
			else filter).toInt()
		)
		remainder = remainder ushr 7
	} while (remainder != 0L)
	return out.toByteArray()
}

fun varNi(n: Int): ByteArray = varNl(n.toLong() and 0xFFFFFFFF)

val encoder: CharsetEncoder = Charsets.UTF_8.newEncoder()
fun string(s: String): ByteArray {
	val bufferA = encoder.encode(CharBuffer.wrap(s))
	val written = bufferA.limit()
	val bufferB = ByteBuffer.allocate(varSizeOfNi(written) + bufferA.limit())
	bufferB.put(varNi(written))
	bufferB.put(bufferA)
	return bufferB.array()
}