package org.bread_experts_group.model.natives.nt.library

import org.bread_experts_group.model.natives.Library
import org.bread_experts_group.model.natives.LookupBacked
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.c.int_t
import org.bread_experts_group.model.natives.nt.datatype.*
import org.bread_experts_group.model.natives.nt.datatype.winuser.LPPAINTSTRUCT
import org.bread_experts_group.model.natives.nt.datatype.winuser.PAINTSTRUCT
import org.bread_experts_group.model.natives.nt.datatype.winuser.WNDCLASSEXA
import org.bread_experts_group.model.natives.nt.datatype.winuser.WNDCLASSEXW

@Suppress("FunctionName", "LocalVariableName")
@LookupBacked("User32.dll")
abstract class User32 internal constructor() : Library {
	// TODO: Windows NT 3.51.1039.1
	abstract fun ShowWindow(hWnd: HWND, nCmdShow: int_t): BOOL

	abstract fun DefWindowProcA(hWnd: HWND, Msg: UINT, wParam: WPARAM, lParam: LPARAM): LRESULT
	abstract fun DefWindowProcW(hWnd: HWND, Msg: UINT, wParam: WPARAM, lParam: LPARAM): LRESULT

	abstract fun GetMessageA(lpMsg: LPMSG, hWnd: HWND?, wMsgFilterMin: UINT, wMsgFilterMax: UINT): BOOL
	abstract fun GetMessageW(lpMsg: LPMSG, hWnd: HWND?, wMsgFilterMin: UINT, wMsgFilterMax: UINT): BOOL

	abstract fun TranslateMessage(lpMsg: MSG): BOOL

	abstract fun DispatchMessageA(lpMsg: MSG): LRESULT
	abstract fun DispatchMessageW(lpMsg: MSG): LRESULT

	abstract fun RegisterClassExA(
		unnamedParam1: WNDCLASSEXA
	): ATOM

	abstract fun RegisterClassExW(
		unnamedParam1: WNDCLASSEXW
	): ATOM

	abstract fun CreateWindowExA(
		dwExStyle: DWORD,
		lpClassName: LPCSTR?,
		lpWindowName: LPCSTR?,
		dwStyle: DWORD,
		X: int_t,
		Y: int_t,
		nWidth: int_t,
		nHeight: int_t,
		hWndParent: HWND?,
		hMenu: HMENU?,
		hInstance: HINSTANCE?,
		lpParam: LPVOID?
	): HWND

	abstract fun CreateWindowExW(
		dwExStyle: DWORD,
		lpClassName: LPCWSTR?,
		lpWindowName: LPCWSTR?,
		dwStyle: DWORD,
		X: int_t,
		Y: int_t,
		nWidth: int_t,
		nHeight: int_t,
		hWndParent: HWND?,
		hMenu: HMENU?,
		hInstance: HINSTANCE?,
		lpParam: LPVOID?
	): HWND

	abstract fun PostQuitMessage(nExitCode: int_t)

	abstract fun BeginPaint(hWnd: HWND, lpPaint: LPPAINTSTRUCT): HDC
	abstract fun EndPaint(hWnd: HWND, lpPaint: Pointer<PAINTSTRUCT>): BOOL

	abstract fun FillRect(hDC: HDC, lprc: Pointer<RECT>, hbr: HBRUSH): int_t
	abstract fun InvalidateRect(hWnd: HWND, lpRect: Pointer<RECT>?, bErase: BOOL): BOOL

	abstract fun PrintWindow(hwnd: HWND, hdcBlt: HDC, nFlags: UINT): BOOL

	abstract fun FindWindowW(lpClassName: LPCWSTR?, lpWindowName: LPCWSTR?): HWND
	abstract fun GetDesktopWindow(): HWND
	abstract fun GetDC(hWnd: HWND): HDC
}