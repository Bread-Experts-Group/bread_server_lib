package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.*
import java.lang.foreign.Linker
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

private val kernel32Lookup: SymbolLookup? = globalArena.getLookup("Kernel32.dll")

val gleCapture: Linker.Option = Linker.Option.captureCallState("GetLastError")
val nativeGetLastError: VarHandle = capturedStateLayout.varHandle(groupElement("GetLastError"))

val nativeFormatMessageW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "FormatMessageW", DWORD,
	DWORD, LPCVOID, DWORD,
	DWORD, LPWSTR, DWORD,
	ValueLayout.ADDRESS
)

val nativeLocalFree: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "LocalFree", HLOCAL,
	HLOCAL
)

val nativeGetModuleHandleW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "GetModuleHandleW", HMODULE,
	LPCWSTR
)

val nativeLoadLibraryExW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "LoadLibraryExW",
	arrayOf(
		HMODULE,
		LPCWSTR, HANDLE, DWORD
	),
	listOf(
		gleCapture
	)
)

val nativeGetProcAddress: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "GetProcAddress",
	arrayOf(
		FARPROC,
		HMODULE, LPCSTR
	),
	listOf(
		gleCapture
	)
)

val nativeAllocConsoleWithOptions: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "AllocConsoleWithOptions",
	arrayOf(
		HRESULT,
		ValueLayout.ADDRESS, ValueLayout.ADDRESS
	),
	listOf(
		gleCapture
	)
)

val nativeFreeConsole: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "FreeConsole",
	arrayOf(
		BOOL
	),
	listOf(
		gleCapture
	)
)

val nativeAllocConsole: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "AllocConsole",
	arrayOf(
		BOOL
	),
	listOf(
		gleCapture
	)
)

val nativeCreateFile3: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "CreateFile3",
	arrayOf(
		HANDLE,
		LPCWSTR, DWORD, DWORD, DWORD, ValueLayout.ADDRESS // LPCREATEFILE3_EXTENDED_PARAMETERS
	),
	listOf(
		gleCapture
	)
)

val nativeReadFile: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "ReadFile",
	arrayOf(
		BOOL,
		HANDLE, LPVOID, DWORD, LPDWORD, ValueLayout.ADDRESS // LPOVERLAPPED
	),
	listOf(
		gleCapture
	)
)

val nativeWriteFile: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "WriteFile",
	arrayOf(
		BOOL,
		HANDLE, LPCVOID, DWORD, LPDWORD, ValueLayout.ADDRESS // LPOVERLAPPED
	),
	listOf(
		gleCapture
	)
)

val nativeFlushFileBuffers: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "FlushFileBuffers",
	arrayOf(
		BOOL,
		HANDLE
	),
	listOf(
		gleCapture
	)
)

val nativeGetCommState: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "GetCommState",
	arrayOf(
		BOOL,
		HANDLE, LPDCB
	),
	listOf(
		gleCapture
	)
)

val nativeSetCommState: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "SetCommState",
	arrayOf(
		BOOL,
		HANDLE, LPDCB
	),
	listOf(
		gleCapture
	)
)

val nativeGetStdHandle: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "GetStdHandle",
	arrayOf(
		HANDLE,
		DWORD
	),
	listOf(
		gleCapture
	)
)

val nativeGetConsoleMode: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "GetConsoleMode",
	arrayOf(
		BOOL,
		HANDLE, LPDWORD
	),
	listOf(
		gleCapture
	)
)

val nativeSetConsoleMode: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "SetConsoleMode",
	arrayOf(
		BOOL,
		HANDLE, DWORD
	),
	listOf(
		gleCapture
	)
)

val nativeGetConsoleCP: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "GetConsoleCP",
	arrayOf(
		UINT
	),
	listOf(
		gleCapture
	)
)

val nativeGetConsoleOutputCP: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "GetConsoleOutputCP",
	arrayOf(
		UINT
	),
	listOf(
		gleCapture
	)
)

val nativeSetConsoleCP: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "SetConsoleCP",
	arrayOf(
		BOOL,
		UINT
	),
	listOf(
		gleCapture
	)
)

val nativeSetConsoleOutputCP: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "SetConsoleOutputCP",
	arrayOf(
		BOOL,
		UINT
	),
	listOf(
		gleCapture
	)
)

val nativeGetCPInfoExW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "GetCPInfoExW",
	arrayOf(
		BOOL,
		UINT, DWORD, ValueLayout.ADDRESS /* of LPCPINFOEXW */
	),
	listOf(
		gleCapture
	)
)

val nativeReadConsoleInputExW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "ReadConsoleInputExW",
	arrayOf(
		BOOL,
		HANDLE, ValueLayout.ADDRESS /* of PINPUT_RECORD */, DWORD, LPDWORD, USHORT
	),
	listOf(
		gleCapture
	)
)

val nativeGetNumberOfConsoleInputEvents: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "GetNumberOfConsoleInputEvents",
	arrayOf(
		BOOL,
		HANDLE, LPDWORD
	),
	listOf(
		gleCapture
	)
)

val nativeGetTickCount64: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "GetTickCount64",
	arrayOf(
		ULONGLONG
	),
	listOf()
)

val nativeGetCurrentThread: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "GetCurrentThread",
	arrayOf(
		HANDLE
	),
	listOf()
)

val nativeCloseHandle: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "CloseHandle",
	arrayOf(
		BOOL,
		HANDLE
	),
	listOf(
		gleCapture
	)
)