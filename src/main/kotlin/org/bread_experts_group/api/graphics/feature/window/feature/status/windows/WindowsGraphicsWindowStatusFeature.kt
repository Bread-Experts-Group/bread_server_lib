package org.bread_experts_group.api.graphics.feature.window.feature.status.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowStatusFeature
import org.bread_experts_group.api.graphics.feature.window.feature.status.*
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsGraphicsWindowStatusFeature(private val hWnd: MemorySegment) : GraphicsWindowStatusFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun get(
		vararg features: GraphicsWindowStatusGetFeature
	): List<GraphicsWindowStatusGetData> = Arena.ofConfined().use { placementArena ->
		val placement = placementArena.allocate(WINDOWPLACEMENT)
		WINDOWPLACEMENT_length.set(placement, 0, placement.byteSize().toInt())
		val status = nativeGetWindowPlacement!!.invokeExact(
			capturedStateSegment,
			this.hWnd,
			placement
		) as Int
		if (status == 0) throwLastError()
		val data = mutableListOf<GraphicsWindowStatusGetData>()
		val shown = nativeIsWindowVisible!!.invokeExact(this.hWnd) as Int != 0
		data.add(if (shown) StandardGraphicsWindowStatus.SHOWN else StandardGraphicsWindowStatus.HIDDEN)
		when (WINDOWPLACEMENT_showCmd.get(placement, 0) as Int) {
			2 -> data.add(StandardGraphicsWindowStatus.MINIMIZED)
			3 -> data.add(StandardGraphicsWindowStatus.MAXIMIZED)
		}
		data
	}

	override fun set(vararg features: GraphicsWindowStatusSetFeature): List<GraphicsWindowStatusSetData> {
		val data = mutableListOf<GraphicsWindowStatusSetFeature>()
		val nCmdShow: Int = if (features.contains(StandardGraphicsWindowStatus.HIDDEN)) {
			data.add(StandardGraphicsWindowStatus.HIDDEN)
			0
		} else {
			data.add(StandardGraphicsWindowStatus.SHOWN)
			if (features.contains(StandardGraphicsWindowStatus.MAXIMIZED)) {
				data.add(StandardGraphicsWindowStatus.MAXIMIZED)
				3
			} else if (features.contains(StandardGraphicsWindowStatus.MINIMIZED)) {
				data.add(StandardGraphicsWindowStatus.MINIMIZED)
				2
			} else 1
		}
		nativeShowWindow!!.invokeExact(
			capturedStateSegment,
			this.hWnd, nCmdShow
		) as Int
		return data
	}
}