package org.bread_experts_group.io.reader

import org.bread_experts_group.api.system.socket.BSLSocketConnectionEnded
import org.bread_experts_group.api.system.socket.StandardSocketStatus
import org.bread_experts_group.api.system.socket.feature.SocketReceiveFeature
import org.bread_experts_group.api.system.socket.receive.ReceiveSizeData
import org.bread_experts_group.io.reader.BSLSocketWriter.Companion.reverse
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.nio.ByteOrder

class BSLSocketReader<F : D, D>(
	val reading: SocketReceiveFeature<F, D>,
	private vararg val features: F
) {
	private val rxBuffer = Arena.ofAuto().allocate(1024)
	private var remainingData = 0L
	private var dataPointer = 0L
	private fun prepLength(n: Long) {
		if (remainingData >= n) return
		if (remainingData > 0) {
			MemorySegment.copy(
				rxBuffer, dataPointer,
				rxBuffer, 0,
				remainingData
			)
		}
		dataPointer = 0
		while (remainingData < n) {
			val rxData = reading.receiveSegment(
				rxBuffer.asSlice(dataPointer + remainingData),
				*features
			).block()
			if (rxData.any { it === StandardSocketStatus.CONNECTION_CLOSED }) throw BSLSocketConnectionEnded()
			remainingData += rxData.firstNotNullOf { it as? ReceiveSizeData }.bytes
		}
	}

	var order: ByteOrder = ByteOrder.nativeOrder()

	fun readU8k(): UByte = readS8().toUByte()
	fun readU16k(): UShort = readS16().toUShort()
	fun readU32k(): UInt = readS32().toUInt()
	fun readU64k(): ULong = readS64().toULong()

	fun readU8i(): Int = readS8().toInt() and 0xFF
	fun readU16i(): Int = readS16().toInt() and 0xFFFF
	fun readU32l(): Long = readS32().toLong() and 0xFFFFFFFF

	fun readS8(): Byte {
		prepLength(1)
		remainingData -= 1
		return rxBuffer.get(ValueLayout.JAVA_BYTE, dataPointer++)
	}

	fun readS16(): Short {
		prepLength(2)
		remainingData -= 2
		val s = rxBuffer.get(ValueLayout.JAVA_SHORT_UNALIGNED, dataPointer)
		dataPointer += 2
		return if (order != ByteOrder.nativeOrder()) s.reverse
		else s
	}

	fun readS32(): Int {
		prepLength(4)
		remainingData -= 4
		val i = rxBuffer.get(ValueLayout.JAVA_INT_UNALIGNED, dataPointer)
		dataPointer += 4
		return if (order != ByteOrder.nativeOrder()) i.reverse
		else i
	}

	fun readS64(): Long {
		prepLength(8)
		remainingData -= 8
		val l = rxBuffer.get(ValueLayout.JAVA_LONG_UNALIGNED, dataPointer)
		dataPointer += 8
		return if (order != ByteOrder.nativeOrder()) l.reverse
		else l
	}

	fun readN(n: Int): ByteArray {
		prepLength(n.toLong())
		val data = ByteArray(n)
		MemorySegment.copy(
			rxBuffer, ValueLayout.JAVA_BYTE, dataPointer,
			data, 0, data.size
		)
		remainingData -= n
		dataPointer += n
		return data
	}

	fun skip(n: Long) {
		prepLength(n)
		remainingData -= n
		dataPointer += n
	}
}