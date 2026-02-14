package org.bread_experts_group.ffi.windows

import org.bread_experts_group.generic.Mappable

enum class WindowsStopBits(
	override val id: UByte,
	override val tag: String
) : Mappable<WindowsStopBits, UByte> {
	ONESTOPBOT(0u, "1 stop bit"),
	ONE5STOPBITS(1u, "1.5 stop bits"),
	TWOSTOPBITS(2u, "2 stop bits");

	override fun toString(): String = stringForm()
}