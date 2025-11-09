package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.SymbolLookup
import java.lang.invoke.MethodHandle

const val CRYPTPROTECTMEMORY_BLOCK_SIZE = 16L

private val crypt32Lookup: SymbolLookup? = globalArena.getLookup("Crypt32.dll")

val nativeCryptProtectMemory: MethodHandle? = crypt32Lookup.getDowncall(
	nativeLinker, "CryptProtectMemory", BOOL,
	LPVOID, DWORD, DWORD
)

val nativeCryptUnprotectMemory: MethodHandle? = crypt32Lookup.getDowncall(
	nativeLinker, "CryptUnprotectMemory", BOOL,
	LPVOID, DWORD, DWORD
)