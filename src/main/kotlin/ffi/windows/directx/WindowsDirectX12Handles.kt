package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.windows.HRESULT
import org.bread_experts_group.ffi.windows.REFCLSID
import org.bread_experts_group.ffi.windows.REFIID
import org.bread_experts_group.ffi.windows.WindowsGUID
import java.lang.foreign.*
import java.lang.invoke.MethodHandle

private val handleArena = Arena.ofAuto()
private val d3d12Lookup: SymbolLookup = handleArena.getLookup("D3d12.dll")
private val linker: Linker = Linker.nativeLinker()

@OptIn(ExperimentalUnsignedTypes::class)
val nativeCLSID_D3D12Debug = WindowsGUID(
	0xF2352AEBu,
	0xDD84u,
	0x49FEu,
	ubyteArrayOf(0xB9u, 0x7Bu),
	ubyteArrayOf(0xA9u, 0xDCu, 0xFDu, 0xCCu, 0x1Bu, 0x4Fu)
).allocate(handleArena)

@OptIn(ExperimentalUnsignedTypes::class)
val nativeID3D12Debug = WindowsGUID(
	0x344488B7u,
	0x6846u,
	0x474Bu,
	ubyteArrayOf(0xB9u, 0x89u),
	ubyteArrayOf(0xF0u, 0x27u, 0x44u, 0x82u, 0x45u, 0xE0u)
).allocate(handleArena)

@OptIn(ExperimentalUnsignedTypes::class)
val nativeID3D12Device = WindowsGUID(
	0x189819F1u,
	0x1DB6u,
	0x4B57u,
	ubyteArrayOf(0xBEu, 0x54u),
	ubyteArrayOf(0x18u, 0x21u, 0x33u, 0x9Bu, 0x85u, 0xF7u)
).allocate(handleArena)

@OptIn(ExperimentalUnsignedTypes::class)
val nativeID3D12CommandQueue = WindowsGUID(
	0x0EC870A6u,
	0x5D7Eu,
	0x4C22u,
	ubyteArrayOf(0x8Cu, 0xFCu),
	ubyteArrayOf(0x5Bu, 0xAAu, 0xE0u, 0x76u, 0x16u, 0xEDu)
).allocate(handleArena)

@OptIn(ExperimentalUnsignedTypes::class)
val nativeID3D12DescriptorHeap = WindowsGUID(
	0x8EFB471Du,
	0x616Cu,
	0x4F49u,
	ubyteArrayOf(0x90u, 0xF7u),
	ubyteArrayOf(0x12u, 0x7Bu, 0xB7u, 0x63u, 0xFAu, 0x51u)
).allocate(handleArena)

val nativeD3D12GetInterface: MethodHandle = d3d12Lookup.getDowncall(
	linker, "D3D12GetInterface", HRESULT,
	REFCLSID, REFIID, ValueLayout.ADDRESS // of void*
)

val nativeD3D12CreateDevice: MethodHandle = d3d12Lookup.getDowncall(
	linker, "D3D12CreateDevice", HRESULT,
	ValueLayout.ADDRESS /* of IUnknown* */,
	AddressLayout.JAVA_INT /* of D3D_FEATURE_LEVEL */,
	REFIID,
	ValueLayout.ADDRESS // of void**
)