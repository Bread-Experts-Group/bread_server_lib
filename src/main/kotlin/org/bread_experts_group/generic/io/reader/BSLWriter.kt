package org.bread_experts_group.generic.io.reader

import org.bread_experts_group.api.system.io.SendFeature
import org.bread_experts_group.api.system.io.send.SendSizeData
import org.bread_experts_group.api.system.socket.StandardSocketStatus
import org.bread_experts_group.ffi.autoArena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

class BSLWriter<F : D, D>(
	val writing: SendFeature<F, D>,
	bufferSize: Long = 512,
	private vararg val features: F
) : SequentialDataSink {
	companion object {
		val Short.reverse: Short
			get() = java.lang.Short.reverseBytes(this)
		val Int.reverse: Int
			get() = Integer.reverseBytes(this)
		val Long.reverse: Long
			get() = java.lang.Long.reverseBytes(this)
	}

	override var timeout: Duration = Duration.INFINITE
	private val txBuffer = autoArena.allocate(bufferSize)
	private var usefulData = 0L
	override var order: ByteOrder = ByteOrder.nativeOrder()

	override fun flush(): List<WritingStatus>? {
		val status = mutableListOf<WritingStatus>()
		while (usefulData > 0) {
			val txData = writing.sendSegment(
				txBuffer.reinterpret(usefulData),
				*features
			).block(this.timeout)
			var exit = false
			for (data in txData) when (data) {
				is SendSizeData -> usefulData = max(usefulData - data.bytes, 0)
				StandardSocketStatus.OPERATION_TIMEOUT,
				StandardSocketStatus.CONNECTION_CLOSED -> {
					exit = true
					status.add(data)
				}

				else -> {}
			}
			if (exit) break
		}
		return status.ifEmpty { null }
	}

	override fun write8(s: Byte): List<WritingStatus>? {
		val status = if (usefulData + 1 > txBuffer.byteSize()) flush() else null
		txBuffer.set(
			ValueLayout.JAVA_BYTE, usefulData,
			s
		)
		usefulData += 1
		return status
	}

	override fun write16(s: Short): List<WritingStatus>? {
		val status = if (usefulData + 2 > txBuffer.byteSize()) flush() else null
		txBuffer.set(
			ValueLayout.JAVA_SHORT_UNALIGNED, usefulData,
			if (order != ByteOrder.nativeOrder()) s.reverse else s
		)
		usefulData += 2
		return status
	}

	override fun write32(s: Int): List<WritingStatus>? {
		val status = if (usefulData + 4 > txBuffer.byteSize()) flush() else null
		txBuffer.set(
			ValueLayout.JAVA_INT_UNALIGNED, usefulData,
			if (order != ByteOrder.nativeOrder()) s.reverse else s
		)
		usefulData += 4
		return status
	}

	override fun write64(s: Long): List<WritingStatus>? {
		val status = if (usefulData + 8 > txBuffer.byteSize()) flush() else null
		txBuffer.set(
			ValueLayout.JAVA_LONG_UNALIGNED, usefulData,
			if (order != ByteOrder.nativeOrder()) s.reverse else s
		)
		usefulData += 8
		return status
	}

	override fun write8i(u: Int) = write8(u.toUByte())
	override fun write16i(u: Int) = write16(u.toUShort())
	override fun write32l(u: Long) = write32(u.toUInt())

	override fun write(b: ByteArray, offset: Int, length: Int): List<WritingStatus>? {
		var dataOffset = 0
		while (dataOffset < length) {
			val movable = min(txBuffer.byteSize() - usefulData, (length - dataOffset).toLong()).toInt()
			if (movable == 0) {
				val status = flush()
				if (status != null) return status
				continue
			}
			MemorySegment.copy(
				b, offset + dataOffset,
				txBuffer, ValueLayout.JAVA_BYTE, usefulData,
				movable
			)
			usefulData += movable
			dataOffset += movable
		}
		return null
	}

	override fun write(b: ByteBuffer): List<WritingStatus>? {
		return if (b.hasArray()) this.write(b.array(), b.position(), b.remaining())
		else TODO("!")
	}

	override fun fill(n: Long, v: Byte): List<WritingStatus>? {
		if (n > txBuffer.byteSize()) TODO("ALPHA")
		val status = if (usefulData + n > txBuffer.byteSize()) flush() else null
		txBuffer.asSlice(usefulData, n).fill(v)
		usefulData += n
		return status
	}
}