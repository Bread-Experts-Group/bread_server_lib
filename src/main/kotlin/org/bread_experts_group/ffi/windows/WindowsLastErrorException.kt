package org.bread_experts_group.ffi.windows

import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.ffi.OperatingSystemException

class WindowsLastErrorException(
	code: UInt,
	friendlyMessage: String
) : OperatingSystemException("$code: $friendlyMessage") {
	val error = WindowsLastError.entries.id(code)
}