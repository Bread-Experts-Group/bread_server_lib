package org.bread_experts_group.api.computer.mos6502.register

import org.bread_experts_group.hex

class ShortRegister(val name: String, var value: UShort) {
	override fun toString(): String = "$name [${hex(value)}]"
}