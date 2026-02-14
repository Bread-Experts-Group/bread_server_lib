package org.bread_experts_group.project_incubator.sim2

interface Register8 : Register {
	var b: Byte
	var bu: UByte
		get() = b.toUByte()
		set(value) {
			b = value.toByte()
		}

	open class Basic(override var b: Byte, override val label: String) : Register8 {
		override fun toString(): String = "Register8.Basic($label: $b)"
	}
}