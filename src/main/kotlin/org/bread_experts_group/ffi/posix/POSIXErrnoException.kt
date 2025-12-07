package org.bread_experts_group.ffi.posix

import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.ffi.OperatingSystemException

class POSIXErrnoException(
	code: UInt,
	name: String,
	description: String
) : OperatingSystemException("$code: $name: $description") {
	val error = POSIXErrno.entries.id(code)
}