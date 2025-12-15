package org.bread_experts_group.ffi.windows.setup

import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.GUID
import org.bread_experts_group.ffi.windows.ULONG_PTR
import org.bread_experts_group.ffi.windows.`void*`
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.invoke.VarHandle

@Suppress("ObjectPropertyName")
val _SP_DEVINFO_DATA: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("cbSize"),
	GUID.withName("ClassGuid"),
	DWORD.withName("DevInst"),
	ULONG_PTR.withName("Reserved")
)
val SP_DEVINFO_DATA = _SP_DEVINFO_DATA
val PSP_DEVINFO_DATA = `void*` // _SP_DEVINFO_DATA

val SP_DEVINFO_DATA_cbSize: VarHandle = SP_DEVINFO_DATA.varHandle(groupElement("cbSize"))