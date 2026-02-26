package org.bread_experts_group.ffi.windows.directwrite

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.HRESULT
import org.bread_experts_group.ffi.windows.REFIID
import org.bread_experts_group.ffi.windows.`void*`
import java.lang.foreign.SymbolLookup
import java.lang.invoke.MethodHandle

private val dWriteLookup: SymbolLookup? = globalArena.getLookup("Dwrite.dll")

val nativeDWriteCreateFactory: MethodHandle? = dWriteLookup.getDowncall(
	nativeLinker, "DWriteCreateFactory",
	HRESULT,
	DWRITE_FACTORY_TYPE.withName("factoryType"),
	REFIID.withName("riid"),
	`void*`.withName("factory")
)