package org.bread_experts_group.ffi.windows

import java.lang.foreign.AddressLayout
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.VarHandle

val ICONINFO: StructLayout = MemoryLayout.structLayout(
	BOOL.withName("fIcon"),
	DWORD.withName("xHotspot"),
	DWORD.withName("yHotspot"),
	MemoryLayout.paddingLayout(4),
	HBITMAP.withName("hbmMask"),
	HBITMAP.withName("hbmColor")
)
val PICONINFO: AddressLayout = ValueLayout.ADDRESS
val ICONINFO_fIcon: VarHandle = ICONINFO.varHandle(groupElement("fIcon"))
val ICONINFO_hbmMask: VarHandle = ICONINFO.varHandle(groupElement("hbmMask"))
val ICONINFO_hbmColor: VarHandle = ICONINFO.varHandle(groupElement("hbmColor"))