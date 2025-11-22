package org.bread_experts_group.ffi.windows

import org.bread_experts_group.Mappable

enum class WindowsLastError(override val id: UInt) : Mappable<WindowsLastError, UInt> {
	ERROR_FILE_NOT_FOUND(2u),
	ERROR_INVALID_HANDLE(6u),
	ERROR_NO_MORE_FILES(18u),
	ERROR_HANDLE_EOF(38u),
	ERROR_ALREADY_EXISTS(183u),
	ERROR_DIRECTORY(267u),
	ERROR_NOT_FOUND(1168u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}