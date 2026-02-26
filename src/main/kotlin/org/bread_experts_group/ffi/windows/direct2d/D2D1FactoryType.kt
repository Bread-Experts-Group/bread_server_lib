package org.bread_experts_group.ffi.windows.direct2d

import org.bread_experts_group.api.graphics.feature.direct2d.factory.GraphicsWindowDirect2DFactoryFeature
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.generic.Mappable

enum class D2D1FactoryType(
	override val id: Int
) : Mappable<D2D1FactoryType, Int>, GraphicsWindowDirect2DFactoryFeature {
	D2D1_FACTORY_TYPE_SINGLE_THREADED(0),
	D2D1_FACTORY_TYPE_MULTI_THREADED(1);

	override val tag: String = name
}

val D2D1_FACTORY_TYPE = DWORD