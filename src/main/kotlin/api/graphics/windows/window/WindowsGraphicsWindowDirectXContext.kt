package org.bread_experts_group.api.graphics.windows.window

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowDirectXContext
import java.lang.foreign.Arena
import java.lang.foreign.ValueLayout

class WindowsGraphicsWindowDirectXContext(private val window: WindowsGraphicsWindow) : GraphicsWindowDirectXContext() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	private val arena = Arena.ofAuto()

	override fun open() {
		val d3dRedirection = arena.allocate(ValueLayout.ADDRESS)
//		decodeCOMError(
//			arena,
//			nativeD3D12GetInterface.invokeExact(
//				nativeCLSID_D3D12Debug,
//				nativeID3D12Debug,
//				d3dRedirection
//			) as Int
//		)
//		val d3dDebug = ID3D12Debug(d3dRedirection.get(ValueLayout.ADDRESS, 0))
//		d3dDebug.enableDebugLayer()
//		decodeCOMError(
//			arena,
//			nativeCreateDXGIFactory2.invokeExact(
//				0,
//				nativeIID_IDXGIFactory3,
//				d3dRedirection
//			) as Int
//		)
//		val dxgiFactory3 = IDXGIFactory3(d3dRedirection.get(ValueLayout.ADDRESS, 0))
//		decodeCOMError(
//			arena,
//			nativeD3D12CreateDevice.invokeExact(
//				MemorySegment.NULL,
//				D3DFeatureLevel.D3D_FEATURE_LEVEL_12_0.id.toInt(),
//				nativeID3D12Device,
//				d3dRedirection
//			) as Int
//		)
//		val d3dVideoDevice = ID3D12Device(d3dRedirection.get(ValueLayout.ADDRESS, 0))
//		val commandQueueDesc = arena.allocate(D3D12_COMMAND_QUEUE_DESC)
//		decodeCOMError(
//			arena,
//			d3dVideoDevice.createCommandQueue(
//				commandQueueDesc,
//				nativeID3D12CommandQueue,
//				d3dRedirection
//			)
//		)
//		val d3dCommandQueue = ID3D12CommandQueue(d3dRedirection.get(ValueLayout.ADDRESS, 0))
//		val d3dSwapChainDesc = DXGISwapChainDesc1(arena.allocate(DXGI_SWAP_CHAIN_DESC1))
//		d3dSwapChainDesc.bufferCount = 2u
//		d3dSwapChainDesc.bufferUsage = EnumSet.of(DXGIUsage.DXGI_USAGE_RENDER_TARGET_OUTPUT)
//		d3dSwapChainDesc.format = MappedEnumeration(DXGIFormat.DXGI_FORMAT_B8G8R8A8_UNORM)
//		d3dSwapChainDesc.swapEffect = MappedEnumeration(DXGISwapEffect.DXGI_SWAP_EFFECT_FLIP_SEQUENTIAL)
//		d3dSwapChainDesc.sampleDesc.count = 1u
//		decodeCOMError(
//			arena,
//			dxgiFactory3.createSwapChainForHwnd(
//				d3dCommandQueue.ptr,
//				window.hWnd,
//				d3dSwapChainDesc.ptr,
//				MemorySegment.NULL,
//				MemorySegment.NULL,
//				d3dRedirection
//			)
//		)
//		val d3dSwapChain = IDXGISwapChain3(d3dRedirection.get(ValueLayout.ADDRESS, 0))
//		val d3dDescriptorHeapDesc = D3D12DescriptorHeapDesc(arena.allocate(D3D12_DESCRIPTOR_HEAP_DESC))
//		d3dDescriptorHeapDesc.numDescriptors = 2u
//		d3dDescriptorHeapDesc.type = MappedEnumeration(D3D12DescriptorHeapType.D3D12_DESCRIPTOR_HEAP_TYPE_RTV)
//		decodeCOMError(
//			arena,
//			d3dVideoDevice.createDescriptorHeap(
//				d3dDescriptorHeapDesc.ptr,
//				nativeID3D12DescriptorHeap,
//				d3dRedirection
//			)
//		)
//		val d3dDescriptorHeap = ID3D12DescriptorHeap(d3dRedirection.get(ValueLayout.ADDRESS, 0))
//		val rt = arena.allocate(ValueLayout.ADDRESS)
//		println(rt.get(ValueLayout.ADDRESS, 0))
//		println(d3dDescriptorHeap.getCPUDescriptorHandleForHeapStart(rt))
//		println(rt.get(ValueLayout.ADDRESS, 0))
	}

	override fun close() {
		TODO("Not yet implemented")
	}
}