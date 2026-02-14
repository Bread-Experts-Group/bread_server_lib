package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val gdi32Lookup: SymbolLookup? = globalArena.getLookup("Gdi32.dll")

val nativeSetPixelFormat: MethodHandle? = gdi32Lookup.getDowncall(
	nativeLinker, "SetPixelFormat",
	arrayOf(
		BOOL,
		HDC, ValueLayout.JAVA_INT, ValueLayout.ADDRESS
	),
	listOf(
		gleCapture
	)
)

val nativeChoosePixelFormat: MethodHandle? = gdi32Lookup.getDowncall(
	nativeLinker, "ChoosePixelFormat",
	arrayOf(
		ValueLayout.JAVA_INT,
		HDC, ValueLayout.ADDRESS
	),
	listOf(
		gleCapture
	)
)

val nativeSwapBuffers: MethodHandle? = gdi32Lookup.getDowncall(
	nativeLinker, "SwapBuffers", BOOL,
	HDC
)

val nativeCreateCompatibleDC: MethodHandle? = gdi32Lookup.getDowncall(
	nativeLinker, "CreateCompatibleDC", HDC,
	HDC.withName("hdc")
)

val nativeDeleteDC: MethodHandle? = gdi32Lookup.getDowncall(
	nativeLinker, "DeleteDC", BOOL,
	HDC.withName("hdc")
)

val nativeDeleteObject: MethodHandle? = gdi32Lookup.getDowncall(
	nativeLinker, "DeleteObject", BOOL,
	HGDIOBJ.withName("ho")
)

val nativeCreateCompatibleBitmap: MethodHandle? = gdi32Lookup.getDowncall(
	nativeLinker, "CreateCompatibleBitmap", HBITMAP,
	HDC.withName("hdc"),
	int.withName("cx"),
	int.withName("cy")
)

val nativeCreateDIBSection: MethodHandle? = gdi32Lookup.getDowncall(
	nativeLinker, "CreateDIBSection", HBITMAP,
	HDC.withName("hdc"),
	PBITMAPV5HEADER.withName("pbmi"), // TODO: PBITMAPINFO.withName("pbmi"),
	UINT.withName("usage"),
	`void*`.withName("ppvBits"),
	HANDLE.withName("hSection"),
	DWORD.withName("offset")
)

val nativeCreateBitmap: MethodHandle? = gdi32Lookup.getDowncall(
	nativeLinker, "CreateBitmap", HBITMAP,
	int.withName("nWidth"),
	int.withName("nHeight"),
	UINT.withName("nPlanes"),
	UINT.withName("nBitCount"),
	`void*`.withName("lpBits")
)

val nativeSetPixelV: MethodHandle? = gdi32Lookup.getDowncall(
	nativeLinker, "SetPixelV", BOOL,
	HDC.withName("hdc"),
	int.withName("x"),
	int.withName("y"),
	COLORREF.withName("color")
)

val nativeSelectObject: MethodHandle? = gdi32Lookup.getDowncall(
	nativeLinker, "SelectObject", HGDIOBJ,
	HDC.withName("hdc"),
	HGDIOBJ.withName("h")
)