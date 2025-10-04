package org.bread_experts_group.ffi.windows

import org.bread_experts_group.coder.MappedEnumeration
import org.bread_experts_group.ffi.OperatingSystemException

class WindowsNTRESULTException(val result: MappedEnumeration<UInt, WindowsNTStatus>) : OperatingSystemException(
	result.toString()
)