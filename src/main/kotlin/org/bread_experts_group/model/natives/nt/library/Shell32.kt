package org.bread_experts_group.model.natives.nt.library

import org.bread_experts_group.model.natives.Library
import org.bread_experts_group.model.natives.LookupBacked
import org.bread_experts_group.model.natives.c.int_t
import org.bread_experts_group.model.natives.nt.datatype.*

@Suppress("FunctionName")
@LookupBacked("Shell32.dll")
abstract class Shell32 internal constructor() : Library {
	// TODO: Windows 2000 (5.0)
	abstract fun SHGetFolderPathA(
		hwnd: HWND,
		csidl: int_t,
		hToken: HANDLE,
		dwFlags: DWORD,
		pszPath: LPSTR
	): SHFOLDERAPI

	abstract fun SHGetFolderPathW(
		hwnd: HWND,
		csidl: int_t,
		hToken: HANDLE,
		dwFlags: DWORD,
		pszPath: LPWSTR
	): SHFOLDERAPI

	// TODO: Windows Vista (6.0)
//	abstract fun SHGetKnownFolderPath(
//		rfid: REFKNOWNFOLDERID,
//		dwFlags: DWORD,
//		hToken: HANDLE?,
//		ppszPath: SegmentReference<PWSTR>
//	): HRESULT
}