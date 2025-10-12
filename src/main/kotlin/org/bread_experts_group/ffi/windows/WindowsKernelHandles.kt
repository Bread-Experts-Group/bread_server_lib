package org.bread_experts_group.ffi.windows

import org.bread_experts_group.Flaggable.Companion.raw
import org.bread_experts_group.ffi.capturedStateLayout
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import java.lang.foreign.*
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle
import java.util.*

private val handleArena = Arena.ofAuto()
private val kernel32Lookup: SymbolLookup? = handleArena.getLookup("Kernel32.dll")
private val linker: Linker = Linker.nativeLinker()

val gleCapture: Linker.Option = Linker.Option.captureCallState("GetLastError")
val nativeGetLastError: VarHandle = capturedStateLayout.varHandle(groupElement("GetLastError"))

val nativeFormatMessageW: MethodHandle? = kernel32Lookup.getDowncall(
	linker, "FormatMessageW", DWORD,
	DWORD, LPCVOID, DWORD,
	DWORD, LPWSTR, DWORD,
	ValueLayout.ADDRESS
)

val nativeLocalFree: MethodHandle? = kernel32Lookup.getDowncall(
	linker, "LocalFree", HLOCAL,
	HLOCAL
)

val nativeGetModuleHandleW: MethodHandle? = kernel32Lookup.getDowncall(
	linker, "GetModuleHandleW", HMODULE,
	LPCWSTR
)

val nativeLoadLibraryExW: MethodHandle? = kernel32Lookup.getDowncall(
	linker, "LoadLibraryExW",
	arrayOf(
		HMODULE,
		LPCWSTR, HANDLE, DWORD
	),
	listOf(
		gleCapture
	)
)

val nativeGetProcAddress: MethodHandle? = kernel32Lookup.getDowncall(
	linker, "GetProcAddress",
	arrayOf(
		FARPROC,
		HMODULE, LPCSTR
	),
	listOf(
		gleCapture
	)
)

val nativeCreateFile3: MethodHandle? = kernel32Lookup.getDowncall(
	linker, "CreateFile3",
	arrayOf(
		HANDLE,
		LPCWSTR, DWORD, DWORD, DWORD, ValueLayout.ADDRESS // LPCREATEFILE3_EXTENDED_PARAMETERS
	),
	listOf(
		gleCapture
	)
)

val nativeReadFile: MethodHandle? = kernel32Lookup.getDowncall(
	linker, "ReadFile",
	arrayOf(
		BOOL,
		HANDLE, LPVOID, DWORD, LPDWORD, ValueLayout.ADDRESS // LPOVERLAPPED
	),
	listOf(
		gleCapture
	)
)

fun createFile3(
	arena: Arena,
	lpFileName: String,
	dwDesiredAccess: EnumSet<WindowsGenericAccessRights>,
	dwShareMode: EnumSet<WindowsFileSharingTypes>,
	dwCreationDisposition: WindowsCreationDisposition,
	//pCreateExParams: Any? // TODO LPCREATEFILE3_EXTENDED_PARAMETERS
): MemorySegment {
	val serialFile = nativeCreateFile3!!.invokeExact(
		capturedStateSegment,
		arena.allocateFrom(lpFileName, Charsets.UTF_16LE),
		dwDesiredAccess.raw().toInt(),
		dwShareMode.raw().toInt(),
		dwCreationDisposition.id.toInt(),
		MemorySegment.NULL
	) as MemorySegment
	if (serialFile == INVALID_HANDLE_VALUE) decodeLastError(arena)
	return serialFile
}

val nativeGetCommState: MethodHandle? = kernel32Lookup.getDowncall(
	linker, "GetCommState",
	arrayOf(
		BOOL,
		HANDLE, LPDCB
	),
	listOf(
		gleCapture
	)
)

val nativeSetCommState: MethodHandle? = kernel32Lookup.getDowncall(
	linker, "SetCommState",
	arrayOf(
		BOOL,
		HANDLE, LPDCB
	),
	listOf(
		gleCapture
	)
)