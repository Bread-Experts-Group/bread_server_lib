package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.windows.HRESULT
import org.bread_experts_group.ffi.windows.REFIID
import org.bread_experts_group.ffi.windows.UINT
import org.bread_experts_group.ffi.windows.decodeCOMError
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.util.*

/**
 * Consult `typedef struct ID3D12DeviceVtbl` @
 * [d3d12.h](https://github.com/microsoft/DirectX-Headers/blob/main/include/directx/d3d12.h)
 */
class ID3D12Device(
	ptr: MemorySegment,
	iUnknownQueryInterfaceVTblIndex: Int,
	iUnknownAddRefVTblIndex: Int,
	iUnknownReleaseVTblIndex: Int,
	iD3D12ObjectGetPrivateDataVTblIndex: Int,
	iD3D12ObjectSetPrivateDataVTblIndex: Int,
	iD3D12ObjectSetPrivateDataInterfaceVTblIndex: Int,
	iD3D12ObjectSetNameVTblIndex: Int,
	getNodeCountVTblIndex: Int,
	createCommandQueueVTblIndex: Int,
	createCommandAllocatorVTblIndex: Int,
	createGraphicsPipelineStateVTblIndex: Int,
	createComputePipelineStateVTblIndex: Int,
	createCommandListVTblIndex: Int,
	checkFeatureSupportVTblIndex: Int,
	createDescriptorHeapVTblIndex: Int,
	vTblReinterpretationLength: Int
) : ID3D12Object(
	ptr,
	iUnknownQueryInterfaceVTblIndex,
	iUnknownAddRefVTblIndex,
	iUnknownReleaseVTblIndex,
	iD3D12ObjectGetPrivateDataVTblIndex,
	iD3D12ObjectSetPrivateDataVTblIndex,
	iD3D12ObjectSetPrivateDataInterfaceVTblIndex,
	iD3D12ObjectSetNameVTblIndex,
	vTblReinterpretationLength
) {
	constructor(ptr: MemorySegment) : this(
		ptr,
		0,
		1,
		2,
		3,
		4,
		5,
		6,
		7,
		8,
		9,
		10,
		11,
		12,
		13,
		14,
		15
	)

	var getNodeCount = {
		TODO("Not yet implemented")
	}

	var createCommandQueue: (MemorySegment, MemorySegment, MemorySegment) -> Int = { p, r, pp ->
		val handle = getVTblAddress(createCommandQueueVTblIndex).getDowncall(
			linker, HRESULT,
			ValueLayout.ADDRESS, ValueLayout.ADDRESS, REFIID, ValueLayout.ADDRESS
		)
		this.createCommandQueue = { p, r, pp -> handle.invokeExact(ptr, p, r, pp) as Int }
		handle.invokeExact(ptr, p, r, pp) as Int
	}

	var createCommandAllocator = {
		TODO("Not yet implemented")
	}

	var createGraphicsPipelineState = {
		TODO("Not yet implemented")
	}

	var createComputePipelineState = {
		TODO("Not yet implemented")
	}

	var createCommandList = {
		TODO("Not yet implemented")
	}

	var checkFeatureSupport: (D3D12Feature, MemorySegment) -> Int = { f, s ->
		val handle = getVTblAddress(checkFeatureSupportVTblIndex).getDowncall(
			linker, HRESULT,
			ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, UINT
		)
		this.checkFeatureSupport = { f, s -> handle.invokeExact(ptr, f.id.toInt(), s, s.byteSize().toInt()) as Int }
		handle.invokeExact(ptr, f.id.toInt(), s, s.byteSize().toInt()) as Int
	}

	fun checkFeatureSupport(
		arena: Arena,
		feature: D3D12Feature,
		structPreprocessing: (D3D12FeatureData) -> Unit = {}
	): D3D12FeatureData = when (feature) {
		D3D12Feature.D3D12_FEATURE_D3D12_OPTIONS -> {
			val struct = D3D12FeatureDataD3D12Options(arena.allocate(D3D12_FEATURE_DATA_D3D12_OPTIONS))
			structPreprocessing(struct)
			decodeCOMError(arena, this.checkFeatureSupport(feature, struct.ptr))
			struct
		}

		D3D12Feature.D3D12_FEATURE_FEATURE_LEVELS -> {
			val struct = D3D12FeatureDataFeatureLevels(arena.allocate(D3D12_FEATURE_DATA_FEATURE_LEVELS))
			val featureLevels = D3DFeatureLevel.entries.map { it.id.toInt() }.toIntArray()
			struct.numFeatureLevels = featureLevels.size.toUInt()
			struct.pFeatureLevelsRequested = arena.allocateFrom(D3D_FEATURE_LEVEL, *featureLevels)
			structPreprocessing(struct)
			decodeCOMError(arena, this.checkFeatureSupport(feature, struct.ptr))
			struct
		}

		D3D12Feature.D3D12_FEATURE_FORMAT_SUPPORT -> {
			val struct = D3D12FeatureDataFormatSupport(arena.allocate(D3D12_FEATURE_DATA_FORMAT_SUPPORT))
			struct.support1 = EnumSet.allOf(D3D12FormatSupport1::class.java)
			struct.support2 = EnumSet.allOf(D3D12FormatSupport2::class.java)
			structPreprocessing(struct)
			decodeCOMError(arena, this.checkFeatureSupport(feature, struct.ptr))
			struct
		}

		else -> TODO("Feature unsupported: $feature")
	}

	var createDescriptorHeap: (MemorySegment, MemorySegment, MemorySegment) -> Int = { pD, r, pp ->
		val handle = getVTblAddress(createDescriptorHeapVTblIndex).getDowncall(
			linker, HRESULT,
			ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS
		)
		this.createDescriptorHeap = { pD, r, pp -> handle.invokeExact(ptr, pD, r, pp) as Int }
		handle.invokeExact(ptr, pD, r, pp) as Int
	}
}