package org.bread_experts_group.api.graphics.feature.directwrite.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.directwrite.GraphicsWindowDirectWriteFeature
import org.bread_experts_group.api.graphics.feature.directwrite.factory.GraphicsWindowDirectWriteFactory
import org.bread_experts_group.api.graphics.feature.directwrite.factory.GraphicsWindowDirectWriteFactoryData
import org.bread_experts_group.api.graphics.feature.directwrite.factory.GraphicsWindowDirectWriteFactoryFeature
import org.bread_experts_group.api.graphics.feature.directwrite.factory.StandardGraphicsWindowDirectWriteFactoryCreationType
import org.bread_experts_group.ffi.threadLocalPTR
import org.bread_experts_group.ffi.windows.directwrite.DWriteFactoryType
import org.bread_experts_group.ffi.windows.directwrite.nativeDWriteCreateFactory
import org.bread_experts_group.ffi.windows.directwrite.nativeIID_IDWriteFactory
import org.bread_experts_group.ffi.windows.tryThrowWin32Error
import org.bread_experts_group.ffi.windows.`void*`

class WindowsGraphicsWindowDirectWriteFeature : GraphicsWindowDirectWriteFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeDWriteCreateFactory != null

	override fun factory(vararg features: GraphicsWindowDirectWriteFactoryFeature): List<GraphicsWindowDirectWriteFactoryData> {
		val data = mutableListOf<GraphicsWindowDirectWriteFactoryData>()
		val type = if (features.contains(StandardGraphicsWindowDirectWriteFactoryCreationType.SHARED)) {
			DWriteFactoryType.DWRITE_FACTORY_TYPE_SHARED
		} else DWriteFactoryType.DWRITE_FACTORY_TYPE_ISOLATED
		tryThrowWin32Error(
			nativeDWriteCreateFactory!!.invokeExact(
				type.id,
				nativeIID_IDWriteFactory,
				threadLocalPTR
			) as Int
		)
		data.add(GraphicsWindowDirectWriteFactory(threadLocalPTR.get(`void*`, 0)))
		return data
	}
}