package org.bread_experts_group.model.natives

import java.lang.foreign.MemorySegment

interface BackingSegment {
	fun getSegment(): MemorySegment
}