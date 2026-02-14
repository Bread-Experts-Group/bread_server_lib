package org.bread_experts_group.ffi.windows

import org.bread_experts_group.generic.FlagSet
import org.bread_experts_group.generic.FlagSetConvertible

enum class WindowsWindowStyles : FlagSetConvertible {
	RESERVED_0,
	RESERVED_1,
	RESERVED_2,
	RESERVED_3,
	RESERVED_4,
	RESERVED_5,
	RESERVED_6,
	RESERVED_7,
	RESERVED_8,
	RESERVED_9,
	RESERVED_10,
	RESERVED_11,
	RESERVED_12,
	RESERVED_13,
	RESERVED_14,
	RESERVED_15,
	WS_TABSTOP,
	WS_GROUP,
	WS_THICKFRAME,
	WS_SYSMENU,
	WS_HSCROLL,
	WS_VSCROLL,
	WS_DLGFRAME,
	WS_BORDER,
	WS_MAXIMIZE,
	WS_CLIPCHILDREN,
	WS_CLIPSIBLINGS,
	WS_DISABLED,
	WS_VISIBLE,
	WS_MINIMIZE,
	WS_CHILD,
	WS_POPUP
}

val WS_MINIMIZEBOX = WindowsWindowStyles.WS_GROUP
val WS_MAXIMIZEBOX = WindowsWindowStyles.WS_TABSTOP

val WS_ICONIC = WindowsWindowStyles.WS_MINIMIZE
val WS_SIZEBOX = WindowsWindowStyles.WS_THICKFRAME

val WS_CAPTION = FlagSet.of(WindowsWindowStyles.WS_BORDER, WindowsWindowStyles.WS_DLGFRAME)

val WS_OVERLAPPEDWINDOW = FlagSet.of(
	WindowsWindowStyles.WS_SYSMENU,
	WindowsWindowStyles.WS_THICKFRAME,
	WS_MINIMIZEBOX,
	WS_MAXIMIZEBOX,
	*WS_CAPTION.toTypedArray()
)

val WS_TILEDWINDOW = WS_OVERLAPPEDWINDOW

val WS_POPUPWINDOW = FlagSet.of(
	WindowsWindowStyles.WS_POPUP,
	WindowsWindowStyles.WS_BORDER,
	WindowsWindowStyles.WS_SYSMENU
)

val WS_CHILDWINDOW = WindowsWindowStyles.WS_CHILD

val CW_USEDEFAULT = 0x80000000.toInt()