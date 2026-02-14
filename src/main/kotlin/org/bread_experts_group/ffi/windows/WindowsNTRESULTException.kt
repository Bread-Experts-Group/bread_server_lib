package org.bread_experts_group.ffi.windows

import org.bread_experts_group.generic.MappedEnumeration
import org.bread_experts_group.ffi.OperatingSystemException

class WindowsNTSTATUSException(val result: MappedEnumeration<UInt, WindowsNTStatus>) : OperatingSystemException(
	result.toString()
)