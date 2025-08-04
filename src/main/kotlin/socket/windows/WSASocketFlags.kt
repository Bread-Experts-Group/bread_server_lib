package org.bread_experts_group.socket.windows

import org.bread_experts_group.coder.Flaggable

enum class WSASocketFlags(override val position: Long) : Flaggable {
	WSA_FLAG_OVERLAPPED(0x01),
	WSA_FLAG_MULTIPOINT_C_ROOT(0x02),
	WSA_FLAG_MULTIPOINT_C_LEAF(0x04),
	WSA_FLAG_MULTIPOINT_D_ROOT(0x08),
	WSA_FLAG_MULTIPOINT_D_LEAF(0x10),
	WSA_FLAG_ACCESS_SYSTEM_SECURITY(0x40),
	WSA_FLAG_NO_HANDLE_INHERIT(0x80)
}