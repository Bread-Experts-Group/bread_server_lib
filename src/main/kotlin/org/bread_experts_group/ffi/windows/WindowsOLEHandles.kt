package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.AddressLayout
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

val IID = GUID
val REFIID: AddressLayout = ValueLayout.ADDRESS // of IID
val REFCLSID = REFIID

private val ole32Lookup: SymbolLookup? = globalArena.getLookup("Ole32.dll")

val nativeCoCreateGuid: MethodHandle? = ole32Lookup.getDowncall(
	nativeLinker, "CoCreateGuid", HRESULT,
	ValueLayout.ADDRESS // of GUID
)