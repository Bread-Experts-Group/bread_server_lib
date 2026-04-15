package org.bread_experts_group.ffi.windows

import org.bread_experts_group.api.graphics.feature.direct2d.brush.GraphicsWindowDirect2DBrush
import org.bread_experts_group.api.graphics.feature.direct2d.brush.GraphicsWindowDirect2DSolidColorBrush
import org.bread_experts_group.api.graphics.feature.direct2d.factory.GraphicsWindowDirect2DFactory
import org.bread_experts_group.api.graphics.feature.direct2d.rendertarget.GraphicsWindowDirect2DHwndRenderTarget
import org.bread_experts_group.api.graphics.feature.direct2d.rendertarget.GraphicsWindowDirect2DRenderTarget
import org.bread_experts_group.api.graphics.feature.direct2d.rendertarget.GraphicsWindowDirect2DResource
import org.bread_experts_group.api.graphics.feature.directwrite.factory.GraphicsWindowDirectWriteFactory
import org.bread_experts_group.api.graphics.feature.directwrite.fontcollection.*
import org.bread_experts_group.api.graphics.feature.directwrite.textformat.DirectWriteTextFormat
import org.bread_experts_group.api.graphics.feature.directwrite.textlayout.DirectWriteTextLayout
import org.bread_experts_group.ffi.windows.directx.*

val vtblFunctionCounts = mapOf<Class<*>, Int>(
	IUnknown::class.java to 3,
	DirectWriteLocalizedStrings::class.java to 6,
	DirectWriteFontFace::class.java to 15,
	DirectWriteFont::class.java to 11,
	DirectWriteFontList::class.java to 3,
	DirectWriteFontFamily::class.java to 3,
	DirectWriteFontCollection::class.java to 4,
	DirectWriteTextFormat::class.java to 25,
	DirectWriteTextLayout::class.java to 39,
	ID3D12Object::class.java to 4,
	IDXGIObject::class.java to 4,
	IDXGIDeviceSubObject::class.java to 1,
	IDXGISwapChain::class.java to 10,
	IDXGISwapChain1::class.java to 11,
	IDXGISwapChain2::class.java to 7,
	IDXGISwapChain3::class.java to 4,
	IDXGIFactory::class.java to 5,
	IDXGIFactory1::class.java to 2,
	IDXGIFactory2::class.java to 11,
	IDXGIFactory3::class.java to 1,
	ID3D12Device::class.java to 9,
	ID3D12DeviceChild::class.java to 1,
	ID3D12Pageable::class.java to 0,
	ID3D12CommandQueue::class.java to 0,
	ID3D12DescriptorHeap::class.java to 3,
	ID3D12Debug::class.java to 1,
	GraphicsWindowDirectWriteFactory::class.java to 21,
	GraphicsWindowDirect2DFactory::class.java to 12,
	GraphicsWindowDirect2DResource::class.java to 1,
	GraphicsWindowDirect2DBrush::class.java to 4,
	GraphicsWindowDirect2DSolidColorBrush::class.java to 2,
	GraphicsWindowDirect2DRenderTarget::class.java to 53,
	GraphicsWindowDirect2DHwndRenderTarget::class.java to 3
)