package org.bread_experts_group.ffi.windows

import org.bread_experts_group.coder.Flaggable

enum class WindowsMessageTypes(override val position: Long) : Flaggable {
	WM_DESTROY(0x0002),
	WM_SETTEXT(0x000C),
	WM_GETTEXT(0x000D),
	WM_GETTEXTLENGTH(0x000E),
	WM_PAINT(0x000F)
}