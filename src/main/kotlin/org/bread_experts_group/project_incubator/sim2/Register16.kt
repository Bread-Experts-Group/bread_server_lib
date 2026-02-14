package org.bread_experts_group.project_incubator.sim2

import kotlin.experimental.and
import kotlin.experimental.or

interface Register16 : Register8, Register {
	var d: Short
	var du: UShort
		get() = d.toUShort()
		set(value) {
			d = value.toShort()
		}

	open class Basic(override var d: Short, override val label: String) : Register16 {
		inner class AliasL8(label: String) : Register8.Basic(-1, label) {
			override var b: Byte
				get() = this@Basic.b
				set(value) {
					this@Basic.b = value
				}

			override fun toString(): String = "Register16.Basic.AliasL8($label: $b [${this@Basic}])"
		}

		inner class AliasH8(label: String) : Register8.Basic(-1, label) {
			override var b: Byte
				get() = ((d.toInt() and 0xFFFF) ushr 8).toByte()
				set(value) {
					d = (d and 0xFF) or (((value.toInt() and 0xFF) shl 8).toShort())
				}

			override fun toString(): String = "Register16.Basic.AliasH8($label: $b [${this@Basic}])"
		}

		override fun toString(): String = "Register16.Basic($label: $d)"
	}

	override var b: Byte
		get() = (d and 0xFF).toByte()
		set(value) {
			d = (d and 0xFF00.toShort()) or (value.toShort() and 0xFF)
		}
}