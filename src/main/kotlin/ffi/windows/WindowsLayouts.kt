package org.bread_experts_group.ffi.windows

import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.VarHandle
import java.sql.Struct

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

val win32PIXELFORMATDESCRIPTOR: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_SHORT.withName("nSize"),
	ValueLayout.JAVA_SHORT.withName("nVersion"),
	ValueLayout.JAVA_INT.withName("dwFlags"),
	ValueLayout.JAVA_BYTE.withName("iPixelType"),
	ValueLayout.JAVA_BYTE.withName("cColorBits"),
	ValueLayout.JAVA_BYTE.withName("cRedBits"),
	ValueLayout.JAVA_BYTE.withName("cRedShift"),
	ValueLayout.JAVA_BYTE.withName("cGreenBits"),
	ValueLayout.JAVA_BYTE.withName("cGreenShift"),
	ValueLayout.JAVA_BYTE.withName("cBlueBits"),
	ValueLayout.JAVA_BYTE.withName("cBlueShift"),
	ValueLayout.JAVA_BYTE.withName("cAlphaBits"),
	ValueLayout.JAVA_BYTE.withName("cAlphaShift"),
	ValueLayout.JAVA_BYTE.withName("cAccumBits"),
	ValueLayout.JAVA_BYTE.withName("cAccumRedBits"),
	ValueLayout.JAVA_BYTE.withName("cAccumGreenBits"),
	ValueLayout.JAVA_BYTE.withName("cAccumBlueBits"),
	ValueLayout.JAVA_BYTE.withName("cAccumAlphaBits"),
	ValueLayout.JAVA_BYTE.withName("cDepthBits"),
	ValueLayout.JAVA_BYTE.withName("cStencilBits"),
	ValueLayout.JAVA_BYTE.withName("cAuxBuffers"),
	ValueLayout.JAVA_BYTE.withName("iLayerType"),
	ValueLayout.JAVA_BYTE.withName("bReserved"),
	ValueLayout.JAVA_INT.withName("dwLayerMask"),
	ValueLayout.JAVA_INT.withName("dwVisibleMask"),
	ValueLayout.JAVA_INT.withName("dwDamageMask"),
)
val win32PIXELFORMATDESCRIPTORnSize: VarHandle = win32PIXELFORMATDESCRIPTOR.varHandle(groupElement("nSize"))
val win32PIXELFORMATDESCRIPTORnVersion: VarHandle = win32PIXELFORMATDESCRIPTOR.varHandle(groupElement("nVersion"))
val win32PIXELFORMATDESCRIPTORdwFlags: VarHandle = win32PIXELFORMATDESCRIPTOR.varHandle(groupElement("dwFlags"))
val win32PIXELFORMATDESCRIPTORiPixelType: VarHandle = win32PIXELFORMATDESCRIPTOR.varHandle(groupElement("iPixelType"))
val win32PIXELFORMATDESCRIPTORcColorBits: VarHandle = win32PIXELFORMATDESCRIPTOR.varHandle(groupElement("cColorBits"))
val win32PIXELFORMATDESCRIPTORcDepthBits: VarHandle = win32PIXELFORMATDESCRIPTOR.varHandle(groupElement("cDepthBits"))

val win32POINT: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("x"),
	ValueLayout.JAVA_INT.withName("y"),
)

val win32MSG: StructLayout = MemoryLayout.structLayout(
	ValueLayout.ADDRESS.withName("hwnd"),
	ValueLayout.JAVA_INT.withName("message"),
	MemoryLayout.paddingLayout(4),
	ValueLayout.JAVA_LONG.withName("wParam"),
	ValueLayout.JAVA_LONG.withName("lParam"),
	ValueLayout.JAVA_INT.withName("time"),
	win32POINT.withName("pt"),
	ValueLayout.JAVA_INT.withName("lPrivate"),
)