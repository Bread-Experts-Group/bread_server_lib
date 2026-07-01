package org.bread_experts_group.model.natives.nt.datatype.winuser

const val WS_OVERLAPPED: Int = 0x00000000
const val WS_SYSMENU: Int = 0x00080000
const val WS_THICKFRAME: Int = 0x00040000
const val WS_BORDER: Int = 0x00800000
const val WS_DLGFRAME: Int = 0x00400000

const val WS_CAPTION: Int = WS_BORDER or WS_DLGFRAME

const val WS_MINIMIZEBOX: Int = 0x00020000
const val WS_MAXIMIZEBOX: Int = 0x00010000

const val WS_OVERLAPPEDWINDOW: Int = WS_OVERLAPPED or
		WS_CAPTION or
		WS_SYSMENU or
		WS_THICKFRAME or
		WS_MINIMIZEBOX or
		WS_MAXIMIZEBOX

const val CW_USEDEFAULT: Int = 0x80000000.toInt()

const val WM_DESTROY = 0x0002
const val WM_PAINT = 0x000F