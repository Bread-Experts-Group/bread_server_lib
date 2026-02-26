package org.bread_experts_group.api.graphics.feature.direct2d.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.direct2d.GraphicsWindowDirect2DFeature
import org.bread_experts_group.api.graphics.feature.direct2d.factory.GraphicsWindowDirect2DFactory
import org.bread_experts_group.api.graphics.feature.direct2d.factory.GraphicsWindowDirect2DFactoryData
import org.bread_experts_group.api.graphics.feature.direct2d.factory.GraphicsWindowDirect2DFactoryFeature
import org.bread_experts_group.ffi.threadLocalPTR
import org.bread_experts_group.ffi.windows.direct2d.D2D1FactoryType
import org.bread_experts_group.ffi.windows.direct2d.nativeD2D1CreateFactory
import org.bread_experts_group.ffi.windows.direct2d.nativeIID_ID2D1Factory
import org.bread_experts_group.ffi.windows.tryThrowWin32Error
import org.bread_experts_group.ffi.windows.`void*`
import java.lang.foreign.MemorySegment

class WindowsGraphicsWindowDirect2DFeature : GraphicsWindowDirect2DFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeD2D1CreateFactory != null

	override fun factory(vararg features: GraphicsWindowDirect2DFactoryFeature): List<GraphicsWindowDirect2DFactoryData> {
		val data = mutableListOf<GraphicsWindowDirect2DFactoryData>()
		val type = if (features.contains(D2D1FactoryType.D2D1_FACTORY_TYPE_SINGLE_THREADED)) {
			D2D1FactoryType.D2D1_FACTORY_TYPE_SINGLE_THREADED
		} else D2D1FactoryType.D2D1_FACTORY_TYPE_MULTI_THREADED
		tryThrowWin32Error(
			nativeD2D1CreateFactory!!.invokeExact(
				type.id,
				nativeIID_ID2D1Factory,
				MemorySegment.NULL, // TODO: debug options
				threadLocalPTR
			) as Int
		)
		data.add(GraphicsWindowDirect2DFactory(threadLocalPTR.get(`void*`, 0)))
		return data
	}
}