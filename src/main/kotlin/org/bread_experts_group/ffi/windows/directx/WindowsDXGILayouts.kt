package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.Flaggable.Companion.from
import org.bread_experts_group.Flaggable.Companion.raw
import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.ffi.windows.BOOL
import org.bread_experts_group.ffi.windows.UINT
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.MemorySegment
import java.lang.foreign.StructLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle
import java.util.*

val DXGI_SAMPLE_DESC: StructLayout = MemoryLayout.structLayout(
	UINT.withName("Count"),
	UINT.withName("Quality")
)
val DXGI_SAMPLE_DESC_Count: VarHandle =
	DXGI_SAMPLE_DESC.varHandle(groupElement("Count"))
val DXGI_SAMPLE_DESC_Quality: VarHandle =
	DXGI_SAMPLE_DESC.varHandle(groupElement("Quality"))

val DXGI_SWAP_CHAIN_DESC1: StructLayout = MemoryLayout.structLayout(
	UINT.withName("Width"),
	UINT.withName("Height"),
	DXGI_FORMAT.withName("Format"),
	BOOL.withName("Stereo"),
	DXGI_SAMPLE_DESC.withName("SampleDesc"),
	DXGI_USAGE.withName("BufferUsage"),
	UINT.withName("BufferCount"),
	DXGI_SCALING.withName("Scaling"),
	DXGI_SWAP_EFFECT.withName("SwapEffect"),
	DXGI_ALPHA_MODE.withName("AlphaMode"),
	DXGI_SWAP_CHAIN_FLAG.withName("Flags")
)
val DXGI_SWAP_CHAIN_DESC1_Format: VarHandle =
	DXGI_SWAP_CHAIN_DESC1.varHandle(groupElement("Format"))
val DXGI_SWAP_CHAIN_DESC1_SampleDesc: MethodHandle =
	DXGI_SWAP_CHAIN_DESC1.sliceHandle(groupElement("SampleDesc"))
val DXGI_SWAP_CHAIN_DESC1_BufferUsage: VarHandle =
	DXGI_SWAP_CHAIN_DESC1.varHandle(groupElement("BufferUsage"))
val DXGI_SWAP_CHAIN_DESC1_BufferCount: VarHandle =
	DXGI_SWAP_CHAIN_DESC1.varHandle(groupElement("BufferCount"))
val DXGI_SWAP_CHAIN_DESC1_SwapEffect: VarHandle =
	DXGI_SWAP_CHAIN_DESC1.varHandle(groupElement("SwapEffect"))

class DXGISampleDesc(
	val ptr: MemorySegment
) {
	var count: UInt
		get() = (DXGI_SAMPLE_DESC_Count.get(ptr) as Int).toUInt()
		set(value) {
			DXGI_SAMPLE_DESC_Count.set(ptr, 0, value.toInt())
		}
	var quality: UInt
		get() = (DXGI_SAMPLE_DESC_Quality.get(ptr) as Int).toUInt()
		set(value) {
			DXGI_SAMPLE_DESC_Quality.set(ptr, 0, value.toInt())
		}
}

class DXGISwapChainDesc1(
	val ptr: MemorySegment
) {
	var format: MappedEnumeration<UInt, DXGIFormat>
		get() = DXGIFormat.entries.id(
			(DXGI_SWAP_CHAIN_DESC1_Format.get(ptr) as Int).toUInt()
		)
		set(value) {
			DXGI_SWAP_CHAIN_DESC1_Format.set(ptr, 0, value.raw.toInt())
		}
	val sampleDesc: DXGISampleDesc
		get() = DXGISampleDesc(DXGI_SWAP_CHAIN_DESC1_SampleDesc.invokeExact(ptr, 0L) as MemorySegment)
	var bufferUsage: EnumSet<DXGIUsage>
		get() = DXGIUsage.entries.from(
			DXGI_SWAP_CHAIN_DESC1_BufferUsage.get(ptr) as Int
		)
		set(value) {
			DXGI_SWAP_CHAIN_DESC1_BufferUsage.set(ptr, 0, value.raw().toInt())
		}
	var bufferCount: UInt
		get() = (DXGI_SWAP_CHAIN_DESC1_BufferCount.get(ptr) as Int).toUInt()
		set(value) {
			DXGI_SWAP_CHAIN_DESC1_BufferCount.set(ptr, 0, value.toInt())
		}
	var swapEffect: MappedEnumeration<UInt, DXGISwapEffect>
		get() = DXGISwapEffect.entries.id(
			(DXGI_SWAP_CHAIN_DESC1_SwapEffect.get(ptr) as Int).toUInt()
		)
		set(value) {
			DXGI_SWAP_CHAIN_DESC1_SwapEffect.set(ptr, 0, value.raw.toInt())
		}
}