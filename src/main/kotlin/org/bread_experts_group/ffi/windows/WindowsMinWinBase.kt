package org.bread_experts_group.ffi.windows

import java.lang.foreign.MemoryLayout
import java.lang.foreign.StructLayout

val _OVERLAPPED: StructLayout = MemoryLayout.structLayout(
	ULONG_PTR.withName("Internal"),
	ULONG_PTR.withName("InternalHigh"),
	MemoryLayout.unionLayout(
		MemoryLayout.structLayout(
			DWORD.withName("Offset"),
			DWORD.withName("OffsetHigh")
		).withName("DUMMYSTRUCTNAME"),
		PVOID.withName("Pointer")
	).withName("DUMMYUNIONNAME"),
	HANDLE.withName("hEvent")
)
val OVERLAPPED = _OVERLAPPED
val LPOVERLAPPED = `void*` // OVERLAPPED