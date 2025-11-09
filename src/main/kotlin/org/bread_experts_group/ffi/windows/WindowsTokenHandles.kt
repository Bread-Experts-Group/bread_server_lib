package org.bread_experts_group.ffi.windows

import java.lang.foreign.MemorySegment

val CURRENT_THREAD_EFFECTIVE_TOKEN: MemorySegment = MemorySegment.ofAddress(-6)
val CURRENT_THREAD_TOKEN: MemorySegment = MemorySegment.ofAddress(-5)
val CURRENT_PROCESS_TOKEN: MemorySegment = MemorySegment.ofAddress(-4)