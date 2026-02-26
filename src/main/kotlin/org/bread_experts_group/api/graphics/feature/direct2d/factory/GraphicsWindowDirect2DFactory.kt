package org.bread_experts_group.api.graphics.feature.direct2d.factory

import org.bread_experts_group.api.graphics.feature.direct2d.factory.cwrtgt.GWD2DCreateWindowRenderTargetData
import org.bread_experts_group.api.graphics.feature.direct2d.factory.cwrtgt.GWD2DCreateWindowRenderTargetFeature
import org.bread_experts_group.api.graphics.feature.direct2d.rendertarget.GraphicsWindowDirect2DHwndRenderTarget
import org.bread_experts_group.api.graphics.feature.window.windows.WindowsGraphicsWindow
import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.threadLocalPTR
import org.bread_experts_group.ffi.windows.HRESULT
import org.bread_experts_group.ffi.windows.direct2d.*
import org.bread_experts_group.ffi.windows.directx.IUnknown
import org.bread_experts_group.ffi.windows.tryThrowWin32Error
import org.bread_experts_group.ffi.windows.`void*`
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle

class GraphicsWindowDirect2DFactory(
	handle: MemorySegment
) : IUnknown(
	handle
), GraphicsWindowDirect2DFactoryData {
	var createHwndRenderTarget: (MemorySegment, MemorySegment, MemorySegment) -> Int = { rtp, hrtp, hrt ->
		val nativeCreateHwndRenderTarget: MethodHandle = getLocalVTblAddress(
			GraphicsWindowDirect2DFactory::class.java, 11
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			PD2D1_RENDER_TARGET_PROPERTIES.withName("renderTargetProperties"),
			PD2D1_HWND_RENDER_TARGET_PROPERTIES.withName("hwndRenderTargetProperties"),
			PID2D1HwndRenderTarget.withName("hwndRenderTarget")
		)
		createHwndRenderTarget = { rtp, hrtp, hrt ->
			nativeCreateHwndRenderTarget.invokeExact(ptr, rtp, hrtp, hrt) as Int
		}
		nativeCreateHwndRenderTarget.invokeExact(ptr, rtp, hrtp, hrt) as Int
	}

	fun createWindowRenderTarget(
		vararg features: GWD2DCreateWindowRenderTargetFeature
	): List<GWD2DCreateWindowRenderTargetData> = Arena.ofConfined().use { optionsArena ->
		val wWindow = features.firstNotNullOfOrNull { it as? WindowsGraphicsWindow } ?: return emptyList()
		val rtp = optionsArena.allocate(D2D1_RENDER_TARGET_PROPERTIES)
		val hRtp = optionsArena.allocate(D2D1_HWND_RENDER_TARGET_PROPERTIES)
		val data = mutableListOf<GWD2DCreateWindowRenderTargetData>(wWindow)
		D2D1_HWND_RENDER_TARGET_PROPERTIES_hwnd.set(hRtp, 0, wWindow.hWnd)
		val ps = D2D1_HWND_RENDER_TARGET_PROPERTIES_pixelSize.invokeExact(hRtp, 0L) as MemorySegment
		D2D1_SIZE_U_width.set(ps, 0, 512)
		D2D1_SIZE_U_height.set(ps, 0, 512)
		tryThrowWin32Error(this.createHwndRenderTarget(rtp, hRtp, threadLocalPTR))
		data.add(GraphicsWindowDirect2DHwndRenderTarget(threadLocalPTR.get(`void*`, 0)))
		return data
	}
}