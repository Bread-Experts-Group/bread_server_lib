package org.bread_experts_group.ffi.windows

import org.bread_experts_group.coder.Mappable

enum class WindowsRequestToSendControl(
	override val id: UInt,
	override val tag: String
) : Mappable<WindowsRequestToSendControl, UInt> {
	RTS_CONTROL_DISABLE(0x00u, "Request-to-send line disabled"),
	RTS_CONTROL_ENABLE(0x01u, "Request-to-send line enabled"),
	RTS_CONTROL_HANDSHAKE(0x02u, "Request-to-send handshaking"),
	RTS_CONTROL_TOGGLE(0x03u, "Request-to-send when available");

	override fun toString(): String = stringForm()
}