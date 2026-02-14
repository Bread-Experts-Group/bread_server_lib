package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.SymbolLookup
import java.lang.invoke.MethodHandle

private val shell32Lookup: SymbolLookup? = globalArena.getLookup("Shell32.dll")

val nativeSHGetStockIconInfo: MethodHandle? = shell32Lookup.getDowncall(
	nativeLinker, "SHGetStockIconInfo", HRESULT,
	int.withName("siid"),
	UINT.withName("uFlags"),
	PSHSTOCKICONINFO.withName("psii")
)