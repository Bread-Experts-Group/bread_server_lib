package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.SymbolLookup
import java.lang.invoke.MethodHandle

const val CRYPTPROTECTMEMORY_BLOCK_SIZE = 16L

private val handleArena = Arena.ofAuto()
private val crypt32Lookup: SymbolLookup? = handleArena.getLookup("Crypt32.dll")
private val linker: Linker = Linker.nativeLinker()

val nativeCryptProtectMemory: MethodHandle? = crypt32Lookup.getDowncall(
	linker, "CryptProtectMemory", BOOL,
	LPVOID, DWORD, DWORD
)

val nativeCryptUnprotectMemory: MethodHandle? = crypt32Lookup.getDowncall(
	linker, "CryptUnprotectMemory", BOOL,
	LPVOID, DWORD, DWORD
)