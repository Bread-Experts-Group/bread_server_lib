package org.bread_experts_group.ffi.windows

import org.bread_experts_group.generic.Flaggable

enum class WindowsGenericAccessRights(override val position: Long) : Flaggable {
	GENERIC_ALL(0x10000000),
	GENERIC_EXECUTE(0x20000000),
	GENERIC_WRITE(0x40000000),
	GENERIC_READ(0x80000000)
}