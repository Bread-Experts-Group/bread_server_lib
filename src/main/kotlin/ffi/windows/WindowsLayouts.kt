package org.bread_experts_group.ffi.windows

import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.VarHandle

val win32GUID: StructLayout = MemoryLayout.structLayout(
	MemoryLayout.sequenceLayout(16, ValueLayout.JAVA_BYTE)
)

val win32WNDCLASSEXA: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("cbSize"),
	ValueLayout.JAVA_INT.withName("style"),
	ValueLayout.ADDRESS.withName("lpfnWndProc"),
	ValueLayout.JAVA_INT.withName("cbClsExtra"),
	ValueLayout.JAVA_INT.withName("cbWndExtra"),
	ValueLayout.ADDRESS.withName("hInstance"),
	ValueLayout.ADDRESS.withName("hIcon"),
	ValueLayout.ADDRESS.withName("hCursor"),
	ValueLayout.ADDRESS.withName("hbrBackground"),
	ValueLayout.ADDRESS.withName("lpszMenuName"),
	ValueLayout.ADDRESS.withName("lpszClassName"),
	ValueLayout.ADDRESS.withName("hIconSm")
)
val win32WNDCLASSEXAcbSize: VarHandle = win32WNDCLASSEXA.varHandle(groupElement("cbSize"))
val win32WNDCLASSEXAlpfnWndProc: VarHandle = win32WNDCLASSEXA.varHandle(groupElement("lpfnWndProc"))
val win32WNDCLASSEXAhInstance: VarHandle = win32WNDCLASSEXA.varHandle(groupElement("hInstance"))
val win32WNDCLASSEXAlpszClassName: VarHandle = win32WNDCLASSEXA.varHandle(groupElement("lpszClassName"))