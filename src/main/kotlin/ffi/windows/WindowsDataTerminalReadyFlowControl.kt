package org.bread_experts_group.ffi.windows

import org.bread_experts_group.coder.Mappable

enum class WindowsDataTerminalReadyFlowControl(
	override val id: UInt,
	override val tag: String
) : Mappable<WindowsDataTerminalReadyFlowControl, UInt> {
	DTR_CONTROL_DISABLE(0x00u, "Data-terminal-ready line disabled"),
	DTR_CONTROL_ENABLE(0x01u, "Data-terminal-ready line enabled"),
	DTR_CONTROL_HANDSHAKE(0x02u, "Data-terminal-ready handshaking");

	override fun toString(): String = stringForm()
}