@file:Suppress("ClassName")

package org.bread_experts_group.ffi.windows.advapi

import org.bread_experts_group.Mappable
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.`void*`

enum class _TOKEN_INFORMATION_CLASS : Mappable<_TOKEN_INFORMATION_CLASS, UInt> {
	TokenUser,
	TokenGroups,
	TokenPrivileges,
	TokenOwner,
	TokenPrimaryGroup,
	TokenDefaultDacl,
	TokenSource,
	TokenType,
	TokenImpersonationLevel,
	TokenStatistics;

	override val id: UInt = (ordinal + 1).toUInt()
	override val tag: String = name
	override fun toString(): String = stringForm()
}

val TOKEN_INFORMATION_CLASS = DWORD
val PTOKEN_INFORMATION_CLASS = `void*` // TOKEN_INFORMATION_CLASS