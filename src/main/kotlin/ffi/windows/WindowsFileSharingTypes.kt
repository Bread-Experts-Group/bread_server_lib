package org.bread_experts_group.ffi.windows

import org.bread_experts_group.coder.Flaggable

enum class WindowsFileSharingTypes(override val position: Long) : Flaggable {
	FILE_SHARE_READ(0x00000001),
	FILE_SHARE_WRITE(0x00000002),
	FILE_SHARE_DELETE(0x00000004)
}