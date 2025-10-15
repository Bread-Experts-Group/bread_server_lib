package org.bread_experts_group.api.compile.ebc.efi

import java.lang.foreign.MemorySegment

interface EFIBootServicesTable {
	val header: EFITableHeader
	fun allocatePool(poolType: EFIMemoryType, size: Long): Pair<Long, MemorySegment>
}