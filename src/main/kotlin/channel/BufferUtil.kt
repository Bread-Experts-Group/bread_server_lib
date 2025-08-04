package org.bread_experts_group.channel

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel

val CRLFi = intArrayOf('\r'.code, '\n'.code)
val SPi = intArrayOf(' '.code)

val ByteBuffer.byteInt: Int
	get() = this.get().toInt() and 0xFF
val ByteBuffer.shortInt: Int
	get() = this.short.toInt() and 0xFFFF
val ByteBuffer.intLong: Long
	get() = this.int.toLong() and 0xFFFFFFFF

fun ByteBuffer.array(n: Int): ByteArray {
	val array = ByteArray(n)
	this.get(array)
	return array
}

fun ReadableByteChannel.transferTo(channel: WritableByteChannel, buffer: ByteBuffer) {
	while (true) {
		buffer.clear()
		val written = this.read(buffer)
		if (written == -1) break
		buffer.flip()
		channel.write(buffer)
	}
}

context(to: WritableByteChannel, buffer: ByteBuffer)
fun ensureCapacity(n: Int) {
	if (buffer.remaining() < n) {
		buffer.flip()
		to.write(buffer)
		buffer.clear()
	}
}

val longBuffer: ByteBuffer = ByteBuffer.allocate(8)
fun Long.bytes(order: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
	longBuffer.order(order)
	longBuffer.putLong(0, this)
	return longBuffer.array()
}