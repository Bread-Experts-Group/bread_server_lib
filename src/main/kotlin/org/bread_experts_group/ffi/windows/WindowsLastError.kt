package org.bread_experts_group.ffi.windows

import org.bread_experts_group.generic.Mappable

enum class WindowsLastError(override val id: UInt) : Mappable<WindowsLastError, UInt> {
	ERROR_SUCCESS(0u),
	ERROR_FILE_NOT_FOUND(2u),
	ERROR_PATH_NOT_FOUND(3u),
	ERROR_ACCESS_DENIED(5u),
	ERROR_INVALID_HANDLE(6u),
	ERROR_NO_MORE_FILES(18u),
	ERROR_SHARING_VIOLATION(32u),
	ERROR_HANDLE_EOF(38u),
	ERROR_NETNAME_DELETED(64u),
	ERROR_INSUFFICIENT_BUFFER(122u),
	ERROR_ALREADY_EXISTS(183u),
	ERROR_DIRECTORY(267u),
	ERROR_IO_PENDING(997u),
	ERROR_NOT_FOUND(1168u),
	WSAECONNABORTED(10053u),
	WSAECONNRESET(10054u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}