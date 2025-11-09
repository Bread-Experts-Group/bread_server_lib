package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.OperatingSystemException

class WindowsLastErrorException(
	val code: UInt,
	val friendlyMessage: String
) : OperatingSystemException("$code: $friendlyMessage")