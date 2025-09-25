package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import java.lang.foreign.*
import java.lang.invoke.MethodHandle

val IID = GUID
val REFIID: AddressLayout = ValueLayout.ADDRESS // of IID
val REFCLSID = REFIID

private val handleArena = Arena.ofAuto()
private val ole32Lookup: SymbolLookup = handleArena.getLookup("Ole32.dll")
private val linker: Linker = Linker.nativeLinker()

val nativeCoCreateGuid: MethodHandle = ole32Lookup.getDowncall(
	linker, "CoCreateGuid", HRESULT,
	ValueLayout.ADDRESS // of GUID
)