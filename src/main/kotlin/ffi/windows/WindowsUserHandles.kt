package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getDowncallVoid
import org.bread_experts_group.ffi.getLookup
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val handleArena = Arena.ofAuto()
private val user32Lookup: SymbolLookup = handleArena.getLookup("User32.dll")
private val linker: Linker = Linker.nativeLinker()

val nativeOpenWindowStationW: MethodHandle = user32Lookup.getDowncall(
	linker, "OpenWindowStationW", HWINSTA,
	LPCWSTR, BOOL, ACCESS_MASK
)

val nativeCloseWindowStation: MethodHandle = user32Lookup.getDowncall(
	linker, "CloseWindowStation", BOOL,
	HWINSTA
)

val nativeGetProcessWindowStation: MethodHandle = user32Lookup.getDowncall(
	linker, "GetProcessWindowStation", HWINSTA
)

val nativeSetProcessWindowStation: MethodHandle = user32Lookup.getDowncall(
	linker, "SetProcessWindowStation", BOOL,
	HWINSTA
)

val nativeOpenDesktopW: MethodHandle = user32Lookup.getDowncall(
	linker, "OpenDesktopW", HDESK,
	LPCWSTR, DWORD, BOOL, ACCESS_MASK
)

val nativeCloseDesktop: MethodHandle = user32Lookup.getDowncall(
	linker, "CloseDesktop", BOOL,
	HDESK
)

val nativeRegisterClassExW: MethodHandle = user32Lookup.getDowncall(
	linker, "RegisterClassExW",
	arrayOf(
		ATOM,
		ValueLayout.ADDRESS // of WNDCLASSEXW
	),
	listOf(
		gleCapture
	)
)

val nativeCreateWindowExW: MethodHandle = user32Lookup.getDowncall(
	linker, "CreateWindowExW",
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

val nativeDefWindowProcW: MethodHandle = user32Lookup.getDowncall(
	linker, "DefWindowProcW", LRESULT,
	HWND, UINT, WPARAM,
	LPARAM
)

val nativeSendMessageW: MethodHandle = user32Lookup.getDowncall(
	linker, "SendMessageW", LRESULT,
	HWND, UINT, WPARAM,
	LPARAM
)

val nativeGetDCEx: MethodHandle = user32Lookup.getDowncall(
	linker, "GetDCEx", HDC,
	HWND, HRGN, DWORD
)

val nativeReleaseDC: MethodHandle = user32Lookup.getDowncall(
	linker, "ReleaseDC", ValueLayout.JAVA_INT,
	HWND, HDC
)

val nativeGetMessageW: MethodHandle = user32Lookup.getDowncall(
	linker, "GetMessageW",
	arrayOf(
		BOOL,
		LPMSG, HWND, UINT,
		UINT
	),
	listOf(
		gleCapture
	)
)

val nativeTranslateMessage: MethodHandle = user32Lookup.getDowncall(
	linker, "TranslateMessage", BOOL,
	ValueLayout.ADDRESS // of MSG
)

val nativeDispatchMessageW: MethodHandle = user32Lookup.getDowncall(
	linker, "DispatchMessageW", LRESULT,
	ValueLayout.ADDRESS // of MSG
)

val nativePostQuitMessage: MethodHandle = user32Lookup.getDowncallVoid(
	linker, "PostQuitMessage", ValueLayout.JAVA_INT
)