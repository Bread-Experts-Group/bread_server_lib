package org.bread_experts_group.ffi.windows

import org.bread_experts_group.coder.Mappable

enum class WindowsCryptProtectMemoryFlags(
	override val id: UInt,
	override val tag: String
) : Mappable<WindowsCryptProtectMemoryFlags, UInt> {
	CRYPTPROTECTMEMORY_SAME_PROCESS(0u, "Same-Process Encrypted"),
	CRYPTPROTECTMEMORY_CROSS_PROCESS(1u, "Cross-Process Encrypted"),
	CRYPTPROTECTMEMORY_SAME_LOGON(2u, "Logon Encrypted"),
}