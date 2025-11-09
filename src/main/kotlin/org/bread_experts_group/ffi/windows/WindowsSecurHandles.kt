package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val secur32Lookup: SymbolLookup? = globalArena.getLookup("Secur32.dll")

val nativeLsaGetLogonSessionData: MethodHandle? = secur32Lookup.getDowncall(
	nativeLinker, "LsaGetLogonSessionData",
	arrayOf(
		NTSTATUS,
		ValueLayout.ADDRESS /* of LUID */, ValueLayout.ADDRESS /* of PSECURITY_LOGON_SESSION_DATA */
	),
	listOf()
)

val nativeLsaFreeReturnBuffer: MethodHandle? = secur32Lookup.getDowncall(
	nativeLinker, "LsaFreeReturnBuffer",
	arrayOf(
		NTSTATUS,
		PVOID
	),
	listOf()
)