package org.bread_experts_group.ffi.windows

import org.bread_experts_group.coder.Mappable

enum class WindowsParityScheme(
	override val id: UByte,
	override val tag: String
) : Mappable<WindowsParityScheme, UByte> {
	NOPARITY(0u, "No parity"),
	ODDPARITY(1u, "Odd parity"),
	EVENPARITY(2u, "Even parity"),
	MARKPARITY(3u, "Mark parity"),
	SPACEPARITY(4u, "Space parity");

	override fun toString(): String = stringForm()
}