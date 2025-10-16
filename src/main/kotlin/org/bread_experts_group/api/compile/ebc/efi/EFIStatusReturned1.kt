package org.bread_experts_group.api.compile.ebc.efi

import java.lang.foreign.MemorySegment

data class EFIStatusReturned1(
	val status: Long,
	val data: MemorySegment
)
