package org.bread_experts_group.ffi.windows

import org.bread_experts_group.coder.Mappable

enum class WindowsCreationDisposition(
	override val id: UInt
) : Mappable<WindowsCreationDisposition, UInt> {
	CREATE_NEW(1u),
	CREATE_ALWAYS(2u),
	OPEN_EXISTING(3u),
	OPEN_ALWAYS(4u),
	TRUNCATE_EXISTING(5u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}