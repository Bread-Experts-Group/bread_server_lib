package org.bread_experts_group.ffi.windows

import org.bread_experts_group.generic.Mappable

enum class WindowsGetFileExInfoLevels(override val id: UInt) : Mappable<WindowsGetFileExInfoLevels, UInt> {
	GetFileExInfoStandard(0u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val GET_FILEEX_INFO_LEVELS = DWORD