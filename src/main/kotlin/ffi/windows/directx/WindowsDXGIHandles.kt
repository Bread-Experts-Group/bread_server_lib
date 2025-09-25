package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.windows.HRESULT
import org.bread_experts_group.ffi.windows.REFIID
import org.bread_experts_group.ffi.windows.UINT
import org.bread_experts_group.ffi.windows.WindowsGUID
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val handleArena = Arena.ofAuto()
private val dxgiLookup: SymbolLookup = handleArena.getLookup("Dxgi.dll")
private val linker: Linker = Linker.nativeLinker()

@OptIn(ExperimentalUnsignedTypes::class)
val nativeIID_IDXGIFactory3 = WindowsGUID(
	0x25483823u,
	0xCD46u,
	0x4C7du,
	ubyteArrayOf(0x86u, 0xCAu),
	ubyteArrayOf(0x47u, 0xAAu, 0x95u, 0xB8u, 0x37u, 0xBDu)
).allocate(handleArena)

val nativeCreateDXGIFactory2: MethodHandle = dxgiLookup.getDowncall(
	linker, "CreateDXGIFactory2", HRESULT,
	UINT, REFIID, ValueLayout.ADDRESS // of IDXGIFactory2**
)