package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.ffi.*
import org.bread_experts_group.ffi.windows.HRESULT
import org.bread_experts_group.ffi.windows.REFIID
import org.bread_experts_group.ffi.windows.UINT
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val dxgiLookup: SymbolLookup? = globalArena.getLookup("Dxgi.dll")

@OptIn(ExperimentalUnsignedTypes::class)
val nativeIID_IDXGIFactory3 = GUID(
	0x25483823u,
	0xCD46u,
	0x4C7du,
	ubyteArrayOf(0x86u, 0xCAu),
	ubyteArrayOf(0x47u, 0xAAu, 0x95u, 0xB8u, 0x37u, 0xBDu)
).allocate(globalArena)

val nativeCreateDXGIFactory2: MethodHandle? = dxgiLookup.getDowncall(
	nativeLinker, "CreateDXGIFactory2", HRESULT,
	UINT, REFIID, ValueLayout.ADDRESS // of IDXGIFactory2**
)