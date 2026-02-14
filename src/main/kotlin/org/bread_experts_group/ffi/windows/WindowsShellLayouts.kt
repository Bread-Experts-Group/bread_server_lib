package org.bread_experts_group.ffi.windows

import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.ValueLayout
import java.lang.invoke.VarHandle

val MAX_PATH = 260L

val SHSTOCKICONINFO = MemoryLayout.structLayout(
	DWORD.withName("cbSize"),
	MemoryLayout.paddingLayout(4),
	HICON.withName("hIcon"),
	int.withName("iSysImageIndex"),
	int.withName("iIcon"),
	MemoryLayout.sequenceLayout(MAX_PATH, WCHAR).withName("szPath")
)
val PSHSTOCKICONINFO = ValueLayout.ADDRESS

val SHSTOCKICONINFO_cbSize: VarHandle = SHSTOCKICONINFO.varHandle(groupElement("cbSize"))
val SHSTOCKICONINFO_hIcon: VarHandle = SHSTOCKICONINFO.varHandle(groupElement("hIcon"))