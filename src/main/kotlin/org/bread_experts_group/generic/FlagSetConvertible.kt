package org.bread_experts_group.generic

interface FlagSetConvertible {
	companion object {
		val Enum<*>.bitL: Long
			get() = 1L shl this.ordinal
		val Enum<*>.bitI: Int
			get() = 1 shl this.ordinal
	}
}