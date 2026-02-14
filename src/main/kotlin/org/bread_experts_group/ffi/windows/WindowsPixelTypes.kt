package org.bread_experts_group.ffi.windows

import org.bread_experts_group.generic.Flaggable

enum class WindowsPixelTypes(override val position: Long) : Flaggable {
	PFD_TYPE_RGBA(0),
	PFD_TYPE_COLORINDEX(1)
}