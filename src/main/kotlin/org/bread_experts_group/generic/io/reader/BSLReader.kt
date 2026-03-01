package org.bread_experts_group.generic.io.reader

import org.bread_experts_group.api.system.io.ReceiveFeature
import org.bread_experts_group.api.system.io.receive.ReceiveSizeData
import org.bread_experts_group.api.system.socket.StandardSocketStatus
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.generic.io.reader.BSLWriter.Companion.reverse
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.time.Duration

class BSLReader<F : D, D>(
	val reading: ReceiveFeature<F, D>,
	bufferSize: Long = 512,
	private val blocking: Boolean = true,
	private vararg val features: F
) : SequentialDataSource {
	override var timeout: Duration = Duration.INFINITE
	private val rxBuffer = autoArena.allocate(bufferSize)
	private var remainingData = 0L
	private var dataPointer = 0L
	private fun prepLength(n: Long): List<ReadingStatus>? {
		if (remainingData >= n) return null
		if (remainingData > 0) MemorySegment.copy(
			rxBuffer, dataPointer,
			rxBuffer, 0,
			remainingData
		)
		dataPointer = 0
		val status = mutableListOf<ReadingStatus>()
		while (remainingData < n) {
			val rxData = reading.receiveSegment(
				rxBuffer.asSlice(remainingData),
				*features
			).block(this.timeout)
			var exit = false
			for (data in rxData) when (data) {
				is ReceiveSizeData -> {
					if (blocking && data.bytes == 0L) {
						status.add(StandardReadingStatuses.INSUFFICIENT_DATA)
						exit = true
					} else remainingData += data.bytes
				}

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

	override var order: ByteOrder = ByteOrder.nativeOrder()

	override fun readS8(): Pair<Byte?, List<ReadingStatus>?> {
		val status = prepLength(1)
		if (status != null) return null to status
		remainingData -= 1
		return rxBuffer.get(ValueLayout.JAVA_BYTE, dataPointer++) to null
	}

	override fun readS16(): Pair<Short?, List<ReadingStatus>?> {
		val status = prepLength(2)
		if (status != null) return null to status
		remainingData -= 2
		val s = rxBuffer.get(ValueLayout.JAVA_SHORT_UNALIGNED, dataPointer)
		dataPointer += 2
		return (if (order != ByteOrder.nativeOrder()) s.reverse else s) to null
	}

	override fun readS32(): Pair<Int?, List<ReadingStatus>?> {
		val status = prepLength(4)
		if (status != null) return null to status
		remainingData -= 4
		val i = rxBuffer.get(ValueLayout.JAVA_INT_UNALIGNED, dataPointer)
		dataPointer += 4
		return (if (order != ByteOrder.nativeOrder()) i.reverse else i) to null
	}

	override fun readS64(): Pair<Long?, List<ReadingStatus>?> {
		val status = prepLength(8)
		if (status != null) return null to status
		remainingData -= 8
		val l = rxBuffer.get(ValueLayout.JAVA_LONG_UNALIGNED, dataPointer)
		dataPointer += 8
		return (if (order != ByteOrder.nativeOrder()) l.reverse else l) to null
	}

	override fun read(into: ByteBuffer): Pair<Int, List<ReadingStatus>?> {
		TODO("! ByteBuffer")
//		prepLength(1)
//		val toWrite = min(into.remaining(), remainingData.toInt())
//		val buffer = rxBuffer.asSlice(dataPointer, remainingData).asByteBuffer()
//			.limit(toWrite)
//		into.put(buffer)
//		remainingData -= toWrite
//		dataPointer += toWrite
	}

	override fun read(into: ByteArray, offset: Int, length: Int): Pair<Int, List<ReadingStatus>?> {
		var lOffset = 0
		while (lOffset < length) {
			val prepped = (length - lOffset).coerceAtMost(rxBuffer.byteSize().toInt())
			val status = prepLength(prepped.toLong())
			MemorySegment.copy(
				rxBuffer, ValueLayout.JAVA_BYTE, dataPointer,
				into, offset + lOffset, prepped
			)
			remainingData -= prepped
			dataPointer += prepped
			lOffset += prepped
			if (status != null) return lOffset to status
		}
		return lOffset to null
	}

	override fun readN(n: Int): Triple<ByteArray, Int, List<ReadingStatus>?> {
		var offset = 0
		val data = ByteArray(n)
		while (offset < n) {
			val prepped = (n - offset).coerceAtMost(rxBuffer.byteSize().toInt())
			val status = prepLength(prepped.toLong())
			MemorySegment.copy(
				rxBuffer, ValueLayout.JAVA_BYTE, dataPointer,
				data, offset, prepped
			)
			remainingData -= prepped
			dataPointer += prepped
			offset += prepped
			if (status != null) return Triple(data, offset, status)
		}
		return Triple(data, offset, null)
	}

	override fun skip(n: Long): Pair<Long, List<ReadingStatus>?> {
		var offset = 0L
		while (offset < n) {
			val prepped = (n - offset).coerceAtMost(rxBuffer.byteSize())
			val status = prepLength(prepped)
			remainingData -= prepped
			dataPointer += prepped
			offset += prepped
			if (status != null) return offset to status
		}
		return offset to null
	}
}