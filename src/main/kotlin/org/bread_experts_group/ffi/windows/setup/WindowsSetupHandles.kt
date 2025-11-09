package org.bread_experts_group.ffi.windows.setup

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val setupAPILookup: SymbolLookup? = globalArena.getLookup("Setupapi.dll")

val HDEVINFO = HANDLE
val nativeSetupDiGetClassDevsW: MethodHandle? = setupAPILookup.getDowncall(
	nativeLinker, "SetupDiGetClassDevsW",
	arrayOf(
		HDEVINFO,
		ValueLayout.ADDRESS /* of GUID */, LPCWSTR, HWND, DWORD
	),
	listOf(
		gleCapture
	)
)

val nativeSetupDiOpenDeviceInfoW: MethodHandle? = setupAPILookup.getDowncall(
	nativeLinker, "SetupDiOpenDeviceInfoW",
	arrayOf(
		BOOL,
		HDEVINFO, LPCWSTR, HWND, DWORD, ValueLayout.ADDRESS /* of SP_DEVINFO_DATA */
	),
	listOf(
		gleCapture
	)
)

val nativeSetupDiGetDevicePropertyW: MethodHandle? = setupAPILookup.getDowncall(
	nativeLinker, "SetupDiGetDevicePropertyW",
	arrayOf(
		BOOL,
		HDEVINFO, ValueLayout.ADDRESS /* of SP_DEVINFO_DATA */, ValueLayout.ADDRESS /* of DEVPROPKEY */,
		ValueLayout.ADDRESS /* of DEVPROPTYPE */, PBYTE, DWORD, PDWORD, DWORD
	),
	listOf(
		gleCapture
	)
)