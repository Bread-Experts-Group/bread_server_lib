package org.bread_experts_group.project_incubator.sim2

interface Register32 : Register16, Register {
	var q: Int
	var qu: UInt
		get() = q.toUInt()
		set(value) {
			q = value.toInt()
		}

	class Basic(override var q: Int, override val label: String) : Register32 {
		inner class AliasL16(label: String) : Register16.Basic(-1, label) {
			override var d: Short
				get() = this@Basic.d
				set(value) {
					this@Basic.d = value
				}

			override fun toString(): String = "Register32.Basic.AliasL16($label: $d [${this@Basic}])"
		}

		override fun toString(): String = "Register32.Basic($label: $q)"
	}

	override var d: Short
		get() = (q and 0xFFFF).toShort()
		set(value) {
			q = (q and 0xFFFF0000.toInt()) or (value.toInt() and 0xFFFF)
		}
}