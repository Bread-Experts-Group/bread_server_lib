package org.bread_experts_group.ffi.windows

import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

const val MAX_PATH = 260L

val SHSTOCKICONINFO: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("cbSize"),
	MemoryLayout.paddingLayout(4),
	HICON.withName("hIcon"),
	int.withName("iSysImageIndex"),
	int.withName("iIcon"),
	MemoryLayout.sequenceLayout(MAX_PATH, WCHAR).withName("szPath")
)
val PSHSTOCKICONINFO = `void*`

val SHSTOCKICONINFO_cbSize: VarHandle = SHSTOCKICONINFO.varHandle(groupElement("cbSize"))
val SHSTOCKICONINFO_hIcon: VarHandle = SHSTOCKICONINFO.varHandle(groupElement("hIcon"))

val SHFILEINFOW: StructLayout = MemoryLayout.structLayout(
	HICON.withName("hIcon"),
	int.withName("iIcon"),
	DWORD.withName("dwAttributes"),
	MemoryLayout.sequenceLayout(MAX_PATH, WCHAR).withName("szDisplayName"),
	MemoryLayout.sequenceLayout(80, WCHAR).withName("szTypeName")
)
val PSHFILEINFOW = `void*`

val SHFILEINFOW_szDisplayName: MethodHandle = SHFILEINFOW.sliceHandle(groupElement("szDisplayName"))
val SHFILEINFOW_szTypeName: MethodHandle = SHFILEINFOW.sliceHandle(groupElement("szTypeName"))