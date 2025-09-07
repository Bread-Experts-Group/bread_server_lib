package org.bread_experts_group.api.computer.arm.v4.register

import org.bread_experts_group.hex

open class Register(val name: String, var value: UInt) {
	override fun toString(): String = "$name [${hex(value)}]"
}