package org.bread_experts_group.generic

class FlagSet<T : Enum<T>>(
	val enum: Class<T>,
	val maskL: Long
) : Collection<T> {
	companion object {
		fun <T : Enum<T>> of(vararg values: T): FlagSet<T> {
			if (values.isEmpty()) throw IllegalArgumentException("must contain at least one value")
			var mask = 0L
			for (value in values) {
				if (value.ordinal > 63) throw IllegalArgumentException("enum range too large")
				mask = mask or (1L shl value.ordinal)
			}
			@Suppress("UNCHECKED_CAST")
			return FlagSet(
				values.first()::class.java as Class<T>,
				mask
			)
		}
	}

	override val size: Int = maskL.countOneBits()
	override fun contains(element: T): Boolean = (maskL and (1L shl element.ordinal)) != 0L
	override fun isEmpty(): Boolean = maskL == 0L
	override fun containsAll(elements: Collection<T>): Boolean {
		var maskedL = 0L
		for (element in elements) maskedL = maskedL or (1L shl element.ordinal)
		return maskL and maskedL == maskedL
	}


	override fun iterator(): Iterator<T> = object : Iterator<T> {
		private var internalMask = maskL
		private var ordinal = 0
		override fun hasNext(): Boolean = internalMask != 0L
		override fun next(): T {
			while (internalMask != 0L) {
				val localOrdinal = ordinal
				val localMask = internalMask
				internalMask = internalMask ushr 1
				ordinal++
				if (localMask and 1L == 1L) return enum.enumConstants.first { it.ordinal == localOrdinal }
			}
			throw NoSuchElementException("mask is 0")
		}
	}

	val maskI: Int
		get() {
			if (maskL ushr 32 != 0L) throw IllegalArgumentException("mask too large")
			return (maskL and 0xFFFFFFFF).toInt()
		}
	val maskS: Short
		get() {
			if (maskL ushr 16 != 0L) throw IllegalArgumentException("mask too large")
			return (maskL and 0xFFFF).toShort()
		}
	val maskB: Byte
		get() {
			if (maskL ushr 8 != 0L) throw IllegalArgumentException("mask too large")
			return (maskL and 0xFF).toByte()
		}
}