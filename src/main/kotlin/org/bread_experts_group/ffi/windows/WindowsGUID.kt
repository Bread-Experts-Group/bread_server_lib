package org.bread_experts_group.ffi.windows

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

@OptIn(ExperimentalUnsignedTypes::class)
data class WindowsGUID(
	val data1: UInt,
	val data2: UShort,
	val data3: UShort,
	val data41: UByteArray,
	val data42: UByteArray,
) {
	fun allocate(arena: Arena): MemorySegment {
		val newGUID = arena.allocate(GUID)
		newGUID.set(ValueLayout.JAVA_INT, 0, data1.toInt())
		newGUID.set(ValueLayout.JAVA_SHORT, 4, data2.toShort())
		newGUID.set(ValueLayout.JAVA_SHORT, 6, data3.toShort())
		(data41 + data42).toByteArray().forEachIndexed { i, b ->
			newGUID.set(ValueLayout.JAVA_BYTE, 8L + i, b)
		}
		return newGUID
	}

	override fun toString(): String = '{' +
			data1.toHexString(HexFormat.UpperCase) + '-' +
			data2.toHexString(HexFormat.UpperCase) + '-' +
			data3.toHexString(HexFormat.UpperCase) + '-' +
			data41.toHexString(HexFormat.UpperCase) + '-' +
			data42.toHexString(HexFormat.UpperCase) +
			'}'

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as WindowsGUID

		if (data1 != other.data1) return false
		if (data2 != other.data2) return false
		if (data3 != other.data3) return false
		if (!data41.contentEquals(other.data41)) return false
		if (!data42.contentEquals(other.data42)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = data1.hashCode()
		result = 31 * result + data2.hashCode()
		result = 31 * result + data3.hashCode()
		result = 31 * result + data41.contentHashCode()
		result = 31 * result + data42.contentHashCode()
		return result
	}
}