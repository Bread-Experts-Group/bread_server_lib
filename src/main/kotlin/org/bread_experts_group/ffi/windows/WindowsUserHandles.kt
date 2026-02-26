package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.*
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val user32Lookup: SymbolLookup? = globalArena.getLookup("User32.dll")

val nativeOpenWindowStationWide: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "OpenWindowStationW", HWINSTA,
	LPCWSTR, BOOL, ACCESS_MASK
)

val nativeCloseWindowStation: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "CloseWindowStation", BOOL,
	HWINSTA
)

val nativeGetProcessWindowStation: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "GetProcessWindowStation", HWINSTA
)

val nativeSetProcessWindowStation: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "SetProcessWindowStation", BOOL,
	HWINSTA
)

val nativeOpenDesktopWide: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "OpenDesktopW", HDESK,
	LPCWSTR, DWORD, BOOL, ACCESS_MASK
)

val nativeCloseDesktop: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "CloseDesktop", BOOL,
	HDESK
)

val nativeRegisterClassExWide: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "RegisterClassExW",
	arrayOf(
		ATOM,
		ValueLayout.ADDRESS // of WNDCLASSEXW
	),
	listOf(
		gleCapture
	)
)

val nativeCreateWindowExWide: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "CreateWindowExW",
	arrayOf(
		HWND,
		DWORD, LPCWSTR, LPCWSTR,
		DWORD, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
		ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, HWND,
		HMENU, HINSTANCE, LPVOID
	),
	listOf(
		gleCapture
	)
)

val nativeDefWindowProcWide: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "DefWindowProcW", LRESULT,
	HWND, UINT, WPARAM,
	LPARAM
)

val nativeSendMessageWide: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "SendMessageW", LRESULT,
	HWND, UINT, WPARAM,
	LPARAM
)

val nativeGetDCEx: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "GetDCEx", HDC,
	HWND, HRGN, DWORD
)

val nativeGetDC: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "GetDC", HDC,
	HWND
)

val nativeReleaseDC: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "ReleaseDC", ValueLayout.JAVA_INT,
	HWND, HDC
)

val nativeGetMessageWide: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "GetMessageW",
	arrayOf(
		BOOL,
		LPMSG, HWND, UINT,
		UINT
	),
	listOf(
		gleCapture
	)
)

val nativeTranslateMessage: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "TranslateMessage", BOOL,
	ValueLayout.ADDRESS // of MSG
)

val nativeDispatchMessageWide: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "DispatchMessageW", LRESULT,
	ValueLayout.ADDRESS // of MSG
)

val nativePostQuitMessage: MethodHandle? = user32Lookup.getDowncallVoid(
	nativeLinker, "PostQuitMessage", ValueLayout.JAVA_INT
)

val nativeLoadIconWide: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "LoadIconW",
	arrayOf(
		HICON,
		HINSTANCE.withName("hInstance"),
		LPCWSTR.withName("lpIconName")
	),
	listOf(gleCapture)
)

val nativeCreateIconIndirect: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "CreateIconIndirect",
	arrayOf(
		HICON,
		PICONINFO.withName("piconinfo")
	),
	listOf(gleCapture)
)

val nativeDestroyIcon: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "DestroyIcon",
	arrayOf(
		BOOL,
		HICON.withName("hIcon")
	),
	listOf(gleCapture)
)

val nativeGetWindowPlacement: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "GetWindowPlacement",
	arrayOf(
		BOOL,
		HWND.withName("hWnd"),
		PWINDOWPLACEMENT.withName("lpwndpl")
	),
	listOf(gleCapture)
)

val nativeShowWindow: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "ShowWindow",
	arrayOf(
		BOOL,
		HWND.withName("hWnd"),
		int.withName("nCmdShow")
	),
	listOf(gleCapture)
)

val nativeSetWindowDisplayAffinity: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "SetWindowDisplayAffinity",
	arrayOf(
		BOOL,
		HWND.withName("hWnd"),
		DWORD.withName("dwAffinity")
	),
	listOf(gleCapture)
)

val nativeGetWindowDisplayAffinity: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "GetWindowDisplayAffinity",
	arrayOf(
		BOOL,
		HWND.withName("hWnd"),
		`void*`.withName("pdwAffinity")
	),
	listOf(gleCapture)
)

val nativeIsWindowVisible: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "IsWindowVisible", BOOL,
	HWND.withName("hWnd")
)

val nativeValidateRect: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "ValidateRect", BOOL,
	HWND.withName("hWnd"),
	PRECT.withName("lpRect")
)

val nativeBeginPaint: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "BeginPaint", HDC,
	HWND.withName("hWnd"),
	LPPAINTSTRUCT.withName("lpPaint")
)

val nativeEndPaint: MethodHandle? = user32Lookup.getDowncall(
	nativeLinker, "EndPaint", BOOL,
	HWND.withName("hWnd"),
	PPAINTSTRUCT.withName("lpPaint")
)