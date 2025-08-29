package org.bread_experts_group.ffi

import java.lang.foreign.MemoryLayout
import java.lang.foreign.StructLayout
import java.lang.foreign.ValueLayout

val win32GUID: StructLayout = MemoryLayout.structLayout(
	MemoryLayout.sequenceLayout(16, ValueLayout.JAVA_BYTE)
)