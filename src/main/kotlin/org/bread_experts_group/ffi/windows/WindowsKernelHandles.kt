package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.*
import java.lang.foreign.Linker
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

private val kernel32Lookup: SymbolLookup? = globalArena.getLookup("Kernel32.dll")

val gleCapture: Linker.Option? = try {
	Linker.Option.captureCallState("GetLastError")
} catch (_: IllegalArgumentException) {
	null
}
val nativeGetLastError: VarHandle by lazy {
	capturedStateLayout.varHandle(groupElement("GetLastError"))
}

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

val nativeCreateDirectory2W: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "CreateDirectory2W",
	arrayOf(
		HANDLE,
		LPCWSTR, DWORD, DWORD, DWORD, ValueLayout.ADDRESS // LPSECURITY_ATTRIBUTES
	),
	listOf(
		gleCapture
	)
)

val nativeFindFirstFileExW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "FindFirstFileExW",
	arrayOf(
		HANDLE,
		LPCWSTR, DWORD, LPVOID, DWORD, LPVOID, DWORD
	),
	listOf(
		gleCapture
	)
)

val nativeFindNextFileW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "FindNextFileW",
	arrayOf(
		BOOL,
		HANDLE, ValueLayout.ADDRESS /* of WIN32_FIND_DATAW */
	),
	listOf(
		gleCapture
	)
)

val nativeFindFirstStreamW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "FindFirstStreamW",
	arrayOf(
		HANDLE,
		LPCWSTR, DWORD, LPVOID, DWORD
	),
	listOf(
		gleCapture
	)
)

val nativeFindNextStreamW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "FindNextStreamW",
	arrayOf(
		BOOL,
		HANDLE, ValueLayout.ADDRESS /* of WIN32_FIND_STREAM_DATA */
	),
	listOf(
		gleCapture
	)
)

val nativeFindClose: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "FindClose",
	arrayOf(
		BOOL,
		HANDLE
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

//val nativeGetCommState: MethodHandle? = kernel32Lookup.getDowncall(
//	nativeLinker, "GetCommState",
//	arrayOf(
//		BOOL,
//		HANDLE, LPDCB
//	),
//	listOf(
//		gleCapture
//	)
//)
//
//val nativeSetCommState: MethodHandle? = kernel32Lookup.getDowncall(
//	nativeLinker, "SetCommState",
//	arrayOf(
//		BOOL,
//		HANDLE, LPDCB
//	),
//	listOf(
//		gleCapture
//	)
//)

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

val nativeGetFileInformationByHandleEx: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "GetFileInformationByHandleEx",
	arrayOf(
		BOOL,
		HANDLE, FILE_INFO_BY_HANDLE_CLASS, LPVOID, DWORD
	),
	listOf(
		gleCapture
	)
)

val nativeGetCurrentDirectoryW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "GetCurrentDirectoryW",
	arrayOf(
		DWORD,
		DWORD, LPWSTR,
	),
	listOf(
		gleCapture
	)
)

val nativeGetTempPath2W: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "GetTempPath2W",
	arrayOf(
		DWORD,
		DWORD, LPWSTR,
	),
	listOf(
		gleCapture
	)
)

val nativeCopyFile2: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "CopyFile2",
	arrayOf(
		HRESULT,
		LPCWSTR, LPCWSTR, ValueLayout.ADDRESS /* of COPYFILE2_EXTENDED_PARAMETERS */
	),
	listOf()
)

val nativeDeleteFile2W: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "DeleteFile2W",
	arrayOf(
		BOOL,
		LPCWSTR, DWORD
	),
	listOf(
		gleCapture
	)
)

val nativeRemoveDirectoryW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "RemoveDirectoryW",
	arrayOf(
		BOOL,
		LPCWSTR
	),
	listOf(
		gleCapture
	)
)

val nativeMoveFileWithProgressW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "MoveFileWithProgressW",
	arrayOf(
		BOOL,
		LPCWSTR, LPCWSTR, ValueLayout.ADDRESS /* of PROGRESS_ROUTINE */, LPVOID, DWORD
	),
	listOf(
		gleCapture
	)
)

val nativeReplaceFileW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "ReplaceFileW",
	arrayOf(
		BOOL,
		LPCWSTR, LPCWSTR, LPCWSTR, DWORD, LPVOID, LPVOID
	),
	listOf(
		gleCapture
	)
)

val nativeCreateSymbolicLinkW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "CreateSymbolicLinkW",
	arrayOf(
		BOOL,
		LPCWSTR, LPCWSTR, DWORD
	),
	listOf(
		gleCapture
	)
)

val nativeCreateHardLinkW: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "CreateHardLinkW",
	arrayOf(
		BOOL,
		LPCWSTR, LPCWSTR, ValueLayout.ADDRESS /* of SECURITY_ATTRIBUTES */
	),
	listOf(
		gleCapture
	)
)

val nativeReOpenFile: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "ReOpenFile",
	arrayOf(
		HANDLE,
		HANDLE, DWORD, DWORD, DWORD
	),
	listOf(
		gleCapture
	)
)

val nativeSetFilePointerEx: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "SetFilePointerEx",
	arrayOf(
		BOOL,
		HANDLE, LARGE_INTEGER, PLARGE_INTEGER, DWORD
	),
	listOf(
		gleCapture
	)
)

val nativeLockFileEx: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "LockFileEx",
	arrayOf(
		BOOL,
		HANDLE, DWORD, DWORD, DWORD, DWORD, ValueLayout.ADDRESS /* of OVERLAPPED */
	),
	listOf(
		gleCapture
	)
)

val nativeUnlockFileEx: MethodHandle? = kernel32Lookup.getDowncall(
	nativeLinker, "UnlockFileEx",
	arrayOf(
		BOOL,
		HANDLE, DWORD, DWORD, DWORD, ValueLayout.ADDRESS /* of OVERLAPPED */
	),
	listOf(
		gleCapture
	)
)