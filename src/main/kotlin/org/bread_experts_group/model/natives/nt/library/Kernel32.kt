package org.bread_experts_group.model.natives.nt.library

import org.bread_experts_group.model.natives.*
import org.bread_experts_group.model.natives.Datatype.Companion.invoke
import org.bread_experts_group.model.natives.nt.datatype.*
import org.bread_experts_group.model.natives.nt.datatype.ioringapi.*
import org.bread_experts_group.model.natives.nt.datatype.ntioring_x.IORING_OP_CODE
import org.bread_experts_group.model.natives.nt.datatype.ntioring_x.IORING_VERSION
import org.bread_experts_group.model.natives.nt.datatype.wtypes.HLOCAL
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment

@Suppress("FunctionName")
@LookupBacked("Kernel32.dll")
abstract class Kernel32 internal constructor() : Library {
	// TODO: Windows NT 3.51
	abstract fun Beep(dwFreq: DWORD, dwDuration: DWORD): BOOL
	abstract fun Sleep(dwMilliseconds: DWORD)
	abstract fun GetTickCount(): DWORD
	abstract fun GetWindowsDirectoryA(lpBuffer: LPSTR, uSize: UINT): UINT
	abstract fun GetWindowsDirectoryW(lpBuffer: LPWSTR, uSize: UINT): UINT
	abstract fun GetLastError(): DWORD
	abstract fun GetCurrentProcessId(): DWORD
	abstract fun GetCurrentThreadId(): DWORD
	abstract fun LocalFree(hMem: HLOCAL): HLOCAL
	abstract fun CreateFileA(
		lpFileName: LPCSTR,
		dwDesiredAccess: DWORD,
		dwShareMode: DWORD,
		lpSecurityAttributes: LPSECURITY_ATTRIBUTES?,
		dwCreationDisposition: DWORD,
		dwFlagsAndAttributes: DWORD,
		hTemplateFile: HANDLE?
	): HANDLE

	abstract fun CreateFileW(
		lpFileName: LPCWSTR,
		dwDesiredAccess: DWORD,
		dwShareMode: DWORD,
		lpSecurityAttributes: LPSECURITY_ATTRIBUTES?,
		dwCreationDisposition: DWORD,
		dwFlagsAndAttributes: DWORD,
		hTemplateFile: HANDLE?
	): HANDLE

	abstract fun FormatMessageA(
		dwFlags: DWORD,
		lpSource: LPCVOID?,
		dwMessageId: DWORD,
		dwLanguageId: DWORD,
		lpBuffer: LPSTR,
		nSize: DWORD,
		@Suppress("LocalVariableName") Arguments: MemorySegment? // TODO: pending varadics
	): DWORD

	abstract fun FormatMessageW(
		dwFlags: DWORD,
		lpSource: LPCVOID?,
		dwMessageId: DWORD,
		dwLanguageId: DWORD,
		lpBuffer: LPWSTR,
		nSize: DWORD,
		@Suppress("LocalVariableName") Arguments: MemorySegment? // TODO: pending varadics
	): DWORD

	abstract fun GetModuleHandleA(lpModuleName: LPCSTR?): HMODULE
	abstract fun GetModuleHandleW(lpModuleName: LPCWSTR?): HMODULE

	// TODO: Windows Vista (6.0)
	abstract fun GetFinalPathNameByHandleA(
		hFile: HANDLE,
		lpszFilePath: LPSTR,
		cchFilePath: DWORD,
		dwFlags: DWORD
	): DWORD

	abstract fun GetFinalPathNameByHandleW(
		hFile: HANDLE,
		lpszFilePath: LPWSTR,
		cchFilePath: DWORD,
		dwFlags: DWORD
	): DWORD

	// TODO: Windows 8 (6.2)
	abstract fun CreateFile2(
		lpFileName: LPCWSTR,
		dwDesiredAccess: DWORD,
		dwShareMode: DWORD,
		dwCreationDisposition: DWORD,
		pCreateExParams: LPCREATEFILE2_EXTENDED_PARAMETERS?
	): HANDLE

	// TODO: Windows 11 24H2 / Windows Server 2025
	abstract fun CreateFile3(
		lpFileName: LPCWSTR,
		dwDesiredAccess: DWORD,
		dwShareMode: DWORD,
		dwCreationDisposition: DWORD,
		pCreateExParams: LPCREATEFILE3_EXTENDED_PARAMETERS?
	): HANDLE

	// TODO: Since at least 10.0.27954.1
	abstract fun QueryIoRingCapabilities(
		capabilities: Pointer<IORING_CAPABILITIES>
	): HRESULT

	abstract fun CreateIoRing(
		ioringVersion: IORING_VERSION,
		flags: IORING_CREATE_FLAGS,
		submissionQueueSize: UINT32,
		completionQueueSize: UINT32,
		h: Pointer<HIORING>
	): HRESULT

	abstract fun GetIoRingInfo(
		ioRing: HIORING,
		info: Pointer<IORING_INFO>
	): HRESULT

	abstract fun IsIoRingOpSupported(
		ioRing: HIORING,
		op: IORING_OP_CODE
	): BOOL

	abstract fun BuildIoRingReadFile(
		ioRing: HIORING,
		fileRef: IORING_HANDLE_REF,
		dataRef: IORING_BUFFER_REF,
		numberOfBytesToRead: UINT32,
		fileOffset: UINT64,
		userData: UINT_PTR,
		sqeFlags: IndexedEnumSet<IORING_SQE_FLAGS>
	): HRESULT

	abstract fun SubmitIoRing(
		ioRing: HIORING,
		waitOperations: UINT32,
		milliseconds: UINT32,
		submittedEntries: Pointer<UINT32>?
	): HRESULT

	abstract fun PopIoRingCompletion(
		ioRing: HIORING,
		cqe: Pointer<IORING_CQE>
	): HRESULT

	companion object {
		fun IoRingHandleRefFromHandle(linker: Linker, h: HANDLE): IORING_HANDLE_REF {
			val ref = Structure.getStructure<IORING_HANDLE_REF>(linker)()
			ref.Kind = IORING_REF_KIND.IORING_REF_RAW
			ref.Handle.Handle = h
			return ref
		}

		fun IoRingHandleRefFromIndex(linker: Linker, i: UINT32): IORING_HANDLE_REF {
			val ref = Structure.getStructure<IORING_HANDLE_REF>(linker)()
			ref.Kind = IORING_REF_KIND.IORING_REF_REGISTERED
			ref.Handle.Index = i
			return ref
		}

		fun IoRingBufferRefFromPointer(linker: Linker, p: MemorySegment): IORING_BUFFER_REF {
			val ref = Structure.getStructure<IORING_BUFFER_REF>(linker)()
			ref.Kind = IORING_REF_KIND.IORING_REF_RAW
			ref.Buffer.Address = p
			return ref
		}

		fun IoRingBufferRefFromIndexAndOffset(linker: Linker, index: UINT32, offset: UINT32): IORING_BUFFER_REF {
			val ref = Structure.getStructure<IORING_BUFFER_REF>(linker)()
			ref.Kind = IORING_REF_KIND.IORING_REF_REGISTERED
			ref.Buffer.IndexAndOffset.BufferIndex = index
			ref.Buffer.IndexAndOffset.Offset = offset
			return ref
		}
	}
}