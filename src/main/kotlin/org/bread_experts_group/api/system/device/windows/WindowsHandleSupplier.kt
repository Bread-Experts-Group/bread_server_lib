package org.bread_experts_group.api.system.device.windows

import java.lang.foreign.MemorySegment

internal interface WindowsHandleSupplier {
	val handle: MemorySegment
}