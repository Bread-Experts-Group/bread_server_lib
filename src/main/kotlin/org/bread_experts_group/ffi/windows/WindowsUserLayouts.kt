package org.bread_experts_group.ffi.windows

import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.invoke.VarHandle

val ICONINFO: StructLayout = MemoryLayout.structLayout(
	BOOL.withName("fIcon"),
	DWORD.withName("xHotspot"),
	DWORD.withName("yHotspot"),
	MemoryLayout.paddingLayout(4),
	HBITMAP.withName("hbmMask"),
	HBITMAP.withName("hbmColor")
)
val PICONINFO = `void*`
val ICONINFO_fIcon: VarHandle = ICONINFO.varHandle(groupElement("fIcon"))
val ICONINFO_hbmMask: VarHandle = ICONINFO.varHandle(groupElement("hbmMask"))
val ICONINFO_hbmColor: VarHandle = ICONINFO.varHandle(groupElement("hbmColor"))

val WINDOWPLACEMENT: StructLayout = MemoryLayout.structLayout(
	UINT.withName("length"),
	UINT.withName("flags"),
	UINT.withName("showCmd"),
	POINT.withName("ptMinPosition"),
	POINT.withName("ptMaxPosition"),
	RECT.withName("rcNormalPosition"),
	RECT.withName("rcDevice")
)
val PWINDOWPLACEMENT = `void*`
val WINDOWPLACEMENT_length: VarHandle = WINDOWPLACEMENT.varHandle(groupElement("length"))
val WINDOWPLACEMENT_showCmd: VarHandle = WINDOWPLACEMENT.varHandle(groupElement("showCmd"))

val PAINTSTRUCT = MemoryLayout.structLayout(
	HDC.withName("hdc"),
	BOOL.withName("fErase"),
	RECT.withName("rcPaint"),
	BOOL.withName("fRestore"),
	BOOL.withName("fIncUpdate"),
	MemoryLayout.sequenceLayout(32, BYTE).withName("rgbReserved")
)
val LPPAINTSTRUCT = `void*`
val PPAINTSTRUCT = `void*`