package org.bread_experts_group.api.system.socket.system.windows

import org.bread_experts_group.ffi.autoArena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

class IdentificationList {
	private var availableIDs = MemorySegment.NULL
	private var extantIDs = MemorySegment.NULL

	private var highest = 0
	private var available = 0

	private val entTypes = ValueLayout.JAVA_INT
	private val intSize = entTypes.byteSize()

	fun getID(): Int = synchronized(extantIDs) {
		val consumed = if (available == 0) highest++
		else availableIDs.get(entTypes, --available * intSize)
		val bytePosition = (consumed ushr 3).toLong()
		val byteShift = consumed and 0b111
		if (bytePosition + 1 > extantIDs.byteSize()) {
			val oldExtantIDs = extantIDs
			extantIDs = autoArena.allocate(extantIDs.byteSize() + 1)
			MemorySegment.copy(
				oldExtantIDs, 0, extantIDs, 0,
				oldExtantIDs.byteSize()
			)
		}
		val extantBits = extantIDs.get(ValueLayout.JAVA_BYTE, bytePosition)
		extantIDs.set(
			ValueLayout.JAVA_BYTE, bytePosition,
			extantBits or (1 shl byteShift).toByte()
		)
		return consumed
	}

	fun returnID(id: Int) = synchronized(extantIDs) {
		val bytePosition = (id ushr 3).toLong()
		if (bytePosition >= extantIDs.byteSize()) throw IllegalArgumentException("$id out of bounds")
		val byteShift = id and 0b111
		val extantBits = extantIDs.get(ValueLayout.JAVA_BYTE, bytePosition)
		if ((extantBits.toInt() and 0xFF) ushr byteShift == 0) throw IllegalArgumentException("$id already free")
		extantIDs.set(
			ValueLayout.JAVA_BYTE, bytePosition,
			extantBits and (1 shl byteShift).toByte().inv()
		)

		val spot = available++ * intSize
		if (spot + intSize > availableIDs.byteSize()) {
			val oldAvailableIDs = availableIDs
			availableIDs = autoArena.allocate(availableIDs.byteSize() + intSize)
			MemorySegment.copy(
				oldAvailableIDs, 0, availableIDs, 0,
				oldAvailableIDs.byteSize()
			)
		}
		availableIDs.set(entTypes, spot, id)
	}
}