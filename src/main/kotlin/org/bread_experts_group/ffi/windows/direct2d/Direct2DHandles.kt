package org.bread_experts_group.ffi.windows.direct2d

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.HRESULT
import org.bread_experts_group.ffi.windows.REFIID
import org.bread_experts_group.ffi.windows.`void*`
import java.lang.foreign.SymbolLookup
import java.lang.invoke.MethodHandle

private val d2d1Lookup: SymbolLookup? = globalArena.getLookup("D2d1.dll")

val nativeD2D1CreateFactory: MethodHandle? = d2d1Lookup.getDowncall(
	nativeLinker, "D2D1CreateFactory",
	HRESULT,
	D2D1_FACTORY_TYPE.withName("factoryType"),
	REFIID.withName("riid"),
	PD2D1_FACTORY_OPTIONS.withName("pFactoryOptions"),
	`void*`.withName("ppIFactory")
)