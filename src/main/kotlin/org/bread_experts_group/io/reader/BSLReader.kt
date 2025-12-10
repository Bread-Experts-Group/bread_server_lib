package org.bread_experts_group.io.reader

import org.bread_experts_group.api.system.io.BSLIODataEnded
import org.bread_experts_group.api.system.io.ReceiveFeature
import org.bread_experts_group.api.system.io.receive.ReceiveSizeData
import org.bread_experts_group.api.system.socket.BSLSocketConnectionEnded
import org.bread_experts_group.api.system.socket.StandardSocketStatus
import org.bread_experts_group.io.reader.BSLWriter.Companion.reverse
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.nio.ByteOrder

class BSLReader<F : D, D>(
	val reading: ReceiveFeature<F, D>,
	val bugCheck: (List<D>, ReceiveSizeData) -> Unit,
	private vararg val features: F
) : SequentialDataSource {
	companion object {
		val socketReadCheck = { data: List<*>, _: ReceiveSizeData ->
			if (data.any { it === StandardSocketStatus.CONNECTION_CLOSED }) throw BSLSocketConnectionEnded()
		}

		val fileReadCheck = { _: List<*>, size: ReceiveSizeData ->
			if (size.bytes == 0L) throw BSLIODataEnded()
		}
	}

	private val rxBuffer = Arena.ofConfined().allocate(65535)
	private var remainingData = 0L
	private var dataPointer = 0L
	private fun prepLength(n: Long) {
		if (remainingData >= n) return
		if (remainingData > 0) MemorySegment.copy(
			rxBuffer, dataPointer,
			rxBuffer, 0,
			remainingData
		)
		dataPointer = 0
		while (remainingData < n) {
			val rxData = reading.receiveSegment(
				rxBuffer.asSlice(remainingData),
				*features
			).block()
			val readSize = rxData.firstNotNullOf { it as? ReceiveSizeData }
			remainingData += readSize.bytes
			bugCheck(rxData, readSize)
		}
	}

	override var order: ByteOrder = ByteOrder.nativeOrder()

	override fun readU8k(): UByte = readS8().toUByte()
	override fun readU16k(): UShort = readS16().toUShort()
	override fun readU32k(): UInt = readS32().toUInt()
	override fun readU64k(): ULong = readS64().toULong()

	override fun readU8i(): Int = readS8().toInt() and 0xFF
	override fun readU16i(): Int = readS16().toInt() and 0xFFFF
	override fun readU32l(): Long = readS32().toLong() and 0xFFFFFFFF

	override fun readS8(): Byte {
		prepLength(1)
		remainingData -= 1
		return rxBuffer.get(ValueLayout.JAVA_BYTE, dataPointer++)
	}

	override fun readS16(): Short {
		prepLength(2)
		remainingData -= 2
		val s = rxBuffer.get(ValueLayout.JAVA_SHORT_UNALIGNED, dataPointer)
		dataPointer += 2
		return if (order != ByteOrder.nativeOrder()) s.reverse
		else s
	}

	override fun readS32(): Int {
		prepLength(4)
		remainingData -= 4
		val i = rxBuffer.get(ValueLayout.JAVA_INT_UNALIGNED, dataPointer)
		dataPointer += 4
		return if (order != ByteOrder.nativeOrder()) i.reverse
		else i
	}

	override fun readS64(): Long {
		prepLength(8)
		remainingData -= 8
		val l = rxBuffer.get(ValueLayout.JAVA_LONG_UNALIGNED, dataPointer)
		dataPointer += 8
		return if (order != ByteOrder.nativeOrder()) l.reverse
		else l
	}

	override fun readN(n: Int): ByteArray {
		var offset = 0L
		val data = ByteArray(n)
		while (offset < n) {
			val prepped = (n - offset).coerceAtMost(rxBuffer.byteSize())
			prepLength(prepped)
			MemorySegment.copy(
				rxBuffer, ValueLayout.JAVA_BYTE, dataPointer,
				data, offset.toInt(), prepped.toInt()
			)
			remainingData -= prepped
			dataPointer += prepped
			offset += prepped
		}
		return data
	}

	override fun skip(n: Long) {
		var offset = 0L
		while (offset < n) {
			val prepped = (n - offset).coerceAtMost(rxBuffer.byteSize())
			prepLength(prepped)
			remainingData -= prepped
			dataPointer += prepped
			offset += prepped
		}
	}
}