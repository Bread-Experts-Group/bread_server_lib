@file:Suppress("ClassName")

package org.bread_experts_group.ffi.windows

import org.bread_experts_group.generic.Mappable

val ACCESS_MASK = DWORD

val PSID = PVOID

enum class _SID_NAME_USE(override val id: UInt) : Mappable<_SID_NAME_USE, UInt> {
	SidTypeUser(1u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val SID_NAME_USE = DWORD
val PSID_NAME_USE = `void*` // SID_NAME_USE