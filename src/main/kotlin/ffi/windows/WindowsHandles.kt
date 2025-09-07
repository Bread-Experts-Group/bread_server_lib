package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val handleArena = Arena.ofAuto()
private val ole32Lookup: SymbolLookup = handleArena.getLookup("Ole32.dll")
private val kernel32Lookup: SymbolLookup = handleArena.getLookup("Kernel32.dll")
private val user32Lookup: SymbolLookup = handleArena.getLookup("User32.dll")
private val linker: Linker = Linker.nativeLinker()

fun makeWord(highByte: UByte, lowByte: UByte): UShort = (highByte.toInt() shl 8 or lowByte.toInt()).toUShort()

val nativeCoCreateGuid: MethodHandle = ole32Lookup.getDowncall(
	linker, "CoCreateGuid", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS
)

val nativeFormatMessageW: MethodHandle = kernel32Lookup.getDowncall(
	linker, "FormatMessageW", ValueLayout.JAVA_INT,
	ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
	ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS
)

val nativeLocalFree: MethodHandle = kernel32Lookup.getDowncall(
	linker, "LocalFree", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS
)

val nativeGetModuleHandleW: MethodHandle = kernel32Lookup.getDowncall(
	linker, "GetModuleHandleW", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS
)

val nativeGetLastError: MethodHandle = kernel32Lookup.getDowncall(
	linker, "GetLastError", ValueLayout.JAVA_INT
)

val nativeOpenWindowStationW: MethodHandle = user32Lookup.getDowncall(
	linker, "OpenWindowStationW", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS, ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_INT
)

val nativeCloseWindowStation: MethodHandle = user32Lookup.getDowncall(
	linker, "CloseWindowStation", ValueLayout.JAVA_BOOLEAN,
	ValueLayout.ADDRESS
)

val nativeGetProcessWindowStation: MethodHandle = user32Lookup.getDowncall(
	linker, "GetProcessWindowStation", ValueLayout.ADDRESS
)

val nativeSetProcessWindowStation: MethodHandle = user32Lookup.getDowncall(
	linker, "SetProcessWindowStation", ValueLayout.JAVA_BOOLEAN,
	ValueLayout.ADDRESS
)

val nativeOpenDesktopW: MethodHandle = user32Lookup.getDowncall(
	linker, "OpenDesktopW", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_INT
)

val nativeCloseDesktop: MethodHandle = user32Lookup.getDowncall(
	linker, "CloseDesktop", ValueLayout.JAVA_BOOLEAN,
	ValueLayout.ADDRESS
)

val nativeRegisterClassExW: MethodHandle = user32Lookup.getDowncall(
	linker, "RegisterClassExW", ValueLayout.JAVA_SHORT,
	ValueLayout.ADDRESS
)

val nativeCreateWindowExW: MethodHandle = user32Lookup.getDowncall(
	linker, "CreateWindowExW", ValueLayout.ADDRESS,
	ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS,
	ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
	ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS
)

val nativeDefWindowProcW: MethodHandle = user32Lookup.getDowncall(
	linker, "DefWindowProcW", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG,
	ValueLayout.JAVA_LONG
)