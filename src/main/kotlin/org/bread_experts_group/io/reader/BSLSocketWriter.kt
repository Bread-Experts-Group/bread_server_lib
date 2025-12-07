package org.bread_experts_group.io.reader

import org.bread_experts_group.api.system.socket.feature.SocketSendFeature
import org.bread_experts_group.api.system.socket.send.SendSizeData
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.nio.ByteOrder

class BSLSocketWriter<F : D, D>(
	val reading: SocketSendFeature<F, D>,
	private vararg val features: F
) {
	companion object {
		val Short.reverse: Short
			get() = java.lang.Short.reverseBytes(this)
		val Int.reverse: Int
			get() = Integer.reverseBytes(this)
		val Long.reverse: Long
			get() = java.lang.Long.reverseBytes(this)
	}

	private val txBuffer = Arena.ofAuto().allocate(65535)
	private var usefulData = 0L
	var order: ByteOrder = ByteOrder.nativeOrder()

	fun flush() {
		while (usefulData > 0) {
			val sendData = reading.sendSegment(txBuffer.reinterpret(usefulData), *features).block()
			usefulData -= sendData.firstNotNullOf { it as? SendSizeData }.bytes
		}
	}

	fun write8(u: UByte) = write8(u.toByte())
	fun write16(u: UShort) = write16(u.toShort())
	fun write32(u: UInt) = write32(u.toInt())
	fun write64(u: ULong) = write64(u.toLong())

	fun write8(s: Byte) {
		if (usefulData + 1 > txBuffer.byteSize()) flush()
		txBuffer.set(
			ValueLayout.JAVA_BYTE, usefulData,
			s
		)
		usefulData += 1
	}

	fun write16(s: Short) {
		if (usefulData + 2 > txBuffer.byteSize()) flush()
		txBuffer.set(
			ValueLayout.JAVA_SHORT_UNALIGNED, usefulData,
			if (order != ByteOrder.nativeOrder()) s.reverse else s
		)
		usefulData += 2
	}

	fun write32(s: Int) {
		if (usefulData + 4 > txBuffer.byteSize()) flush()
		txBuffer.set(
			ValueLayout.JAVA_INT_UNALIGNED, usefulData,
			if (order != ByteOrder.nativeOrder()) s.reverse else s
		)
		usefulData += 4
	}

	fun write64(s: Long) {
		if (usefulData + 8 > txBuffer.byteSize()) flush()
		txBuffer.set(
			ValueLayout.JAVA_LONG_UNALIGNED, usefulData,
			if (order != ByteOrder.nativeOrder()) s.reverse else s
		)
		usefulData += 8
	}

	fun write8i(u: Int) = write8(u.toUByte())
	fun write16i(u: Int) = write16(u.toUShort())
	fun write32l(u: Long) = write32(u.toUInt())

	fun write(b: ByteArray, offset: Int = 0, length: Int = b.size) {
		MemorySegment.copy(
			b, offset,
			txBuffer, ValueLayout.JAVA_BYTE, usefulData,
			length
		)
		usefulData += b.size
	}

	fun fill(n: Long, v: Byte = 0) {
		txBuffer.asSlice(usefulData, n).fill(v)
		usefulData += n
	}
}