package org.bread_experts_group.generic.io

import java.nio.ByteOrder

data class IOEndian(val name: String) {
	companion object {
		val LITTLE: IOEndian = IOEndian("Little")
		val BIG: IOEndian = IOEndian("Big")
		val NATIVE: IOEndian = when (val order = ByteOrder.nativeOrder()) {
			ByteOrder.BIG_ENDIAN -> BIG
			ByteOrder.LITTLE_ENDIAN -> LITTLE
			else -> throw UnsupportedOperationException("Unknown order [$order]")
		}

		val BOTH_LE_BE: IOEndian = IOEndian("Little, Big")
	}
}