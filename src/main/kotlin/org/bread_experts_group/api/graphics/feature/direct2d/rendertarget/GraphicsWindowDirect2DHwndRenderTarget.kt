package org.bread_experts_group.api.graphics.feature.direct2d.rendertarget

import org.bread_experts_group.api.graphics.feature.direct2d.factory.cwrtgt.GWD2DCreateWindowRenderTargetData
import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.HRESULT
import org.bread_experts_group.ffi.windows.direct2d.D2D1_SIZE_U
import org.bread_experts_group.ffi.windows.direct2d.D2D1_SIZE_U_height
import org.bread_experts_group.ffi.windows.direct2d.D2D1_SIZE_U_width
import org.bread_experts_group.ffi.windows.direct2d.PD2D1_SIZE_U
import org.bread_experts_group.ffi.windows.tryThrowWin32Error
import org.bread_experts_group.ffi.windows.`void*`
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle

class GraphicsWindowDirect2DHwndRenderTarget(
	handle: MemorySegment
) : GraphicsWindowDirect2DRenderTarget(
	handle
), GWD2DCreateWindowRenderTargetData {
	var resize: (MemorySegment) -> Int = { s ->
		val nativeResize: MethodHandle = getLocalVTblAddress(
			GraphicsWindowDirect2DHwndRenderTarget::class.java, 1
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			PD2D1_SIZE_U.withName("pixelSize")
		)
		resize = { s ->
			nativeResize.invokeExact(ptr, s) as Int
		}
		nativeResize.invokeExact(ptr, s) as Int
	}

	fun resize(w: Int, h: Int) = Arena.ofConfined().use { sizeArena ->
		val allocated = sizeArena.allocate(D2D1_SIZE_U)
		D2D1_SIZE_U_width.set(allocated, 0, w)
		D2D1_SIZE_U_height.set(allocated, 0, h)
		tryThrowWin32Error(this.resize(allocated))
	}
}