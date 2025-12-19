package org.bread_experts_group.io.reader

import org.bread_experts_group.api.system.io.SendFeature
import org.bread_experts_group.api.system.io.send.SendSizeData
import org.bread_experts_group.api.system.socket.BSLSocketConnectionEnded
import org.bread_experts_group.api.system.socket.StandardSocketStatus
import org.bread_experts_group.api.system.socket.ipv6.send.IPv6SendDataIdentifier
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.nio.ByteOrder

class BSLWriter<F : D, D>(
	val writing: SendFeature<F, D>,
	val bugCheck: (List<D>, SendSizeData) -> Unit,
	private vararg val features: F
) : SequentialDataSink {
	companion object {
		val Short.reverse: Short
			get() = java.lang.Short.reverseBytes(this)
		val Int.reverse: Int
			get() = Integer.reverseBytes(this)
		val Long.reverse: Long
			get() = java.lang.Long.reverseBytes(this)

		val socketWriteCheck = { data: List<IPv6SendDataIdentifier>, _: SendSizeData ->
			if (data.any { it === StandardSocketStatus.CONNECTION_CLOSED }) throw BSLSocketConnectionEnded()
		}
	}

	private val txBuffer = Arena.ofConfined().allocate(65535)
	private var usefulData = 0L
	override var order: ByteOrder = ByteOrder.nativeOrder()

	override fun flush() {
		while (usefulData > 0) {
			val sendData = writing.sendSegment(txBuffer.reinterpret(usefulData), *features).block()
			val sendSize = sendData.firstNotNullOf { it as? SendSizeData }
			usefulData -= sendSize.bytes
			bugCheck(sendData, sendSize)
		}
	}

	override fun write8(s: Byte) {
		if (usefulData + 1 > txBuffer.byteSize()) flush()
		txBuffer.set(
			ValueLayout.JAVA_BYTE, usefulData,
			s
		)
		usefulData += 1
	}

	override fun write16(s: Short) {
		if (usefulData + 2 > txBuffer.byteSize()) flush()
		txBuffer.set(
			ValueLayout.JAVA_SHORT_UNALIGNED, usefulData,
			if (order != ByteOrder.nativeOrder()) s.reverse else s
		)
		usefulData += 2
	}

	override fun write32(s: Int) {
		if (usefulData + 4 > txBuffer.byteSize()) flush()
		txBuffer.set(
			ValueLayout.JAVA_INT_UNALIGNED, usefulData,
			if (order != ByteOrder.nativeOrder()) s.reverse else s
		)
		usefulData += 4
	}

	override fun write64(s: Long) {
		if (usefulData + 8 > txBuffer.byteSize()) flush()
		txBuffer.set(
			ValueLayout.JAVA_LONG_UNALIGNED, usefulData,
			if (order != ByteOrder.nativeOrder()) s.reverse else s
		)
		usefulData += 8
	}

	override fun write8i(u: Int) = write8(u.toUByte())
	override fun write16i(u: Int) = write16(u.toUShort())
	override fun write32l(u: Long) = write32(u.toUInt())

	override fun write(b: ByteArray, offset: Int, length: Int) {
		if (usefulData + length > txBuffer.byteSize()) flush()
		MemorySegment.copy(
			b, offset,
			txBuffer, ValueLayout.JAVA_BYTE, usefulData,
			length
		)
		usefulData += b.size
	}

	override fun fill(n: Long, v: Byte) {
		if (usefulData + n > txBuffer.byteSize()) flush()
		txBuffer.asSlice(usefulData, n).fill(v)
		usefulData += n
	}
}