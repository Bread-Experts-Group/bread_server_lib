package org.bread_experts_group.ffi.windows

import org.bread_experts_group.generic.Flaggable

enum class WindowStationAccessRights(override val position: Long) : Flaggable {
	DELETE(0x00010000),
	READ_CONTROL(0x00020000),
	WRITE_DAC(0x00040000),
	WRITE_OWNER(0x00080000)
}