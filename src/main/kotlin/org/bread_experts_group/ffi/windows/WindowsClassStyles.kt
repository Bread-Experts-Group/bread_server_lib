package org.bread_experts_group.ffi.windows

import org.bread_experts_group.generic.FlagSetConvertible

enum class WindowsClassStyles : FlagSetConvertible {
	CS_VREDRAW,
	CS_HREDRAW,
	RESERVED_2,
	CS_DBLCLKS,
	RESERVED_4,
	CS_OWNDC,
	CS_CLASSDC,
	CS_PARENTDC,
	RESERVED_8,
	CS_NOCLOSE,
	RESERVED_10,
	CS_SAVEBITS,
	CS_BYTEALIGNCLIENT,
	CS_BYTEALIGNWINDOW,
	CS_GLOBALCLASS,
	RESERVED_15,
	CS_IME,
	CS_DROPSHADOW
}