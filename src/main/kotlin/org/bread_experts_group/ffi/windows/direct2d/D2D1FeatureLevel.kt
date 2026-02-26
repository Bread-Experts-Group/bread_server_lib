package org.bread_experts_group.ffi.windows.direct2d

import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.generic.Mappable

enum class D2D1FeatureLevel(override val id: Int) : Mappable<D2D1FeatureLevel, Int> {
	D2D1_FEATURE_LEVEL_DEFAULT(0),
	D2D1_FEATURE_LEVEL_9(1),
	D2D1_FEATURE_LEVEL_10(2);

	override val tag: String = name
}

val D2D1_FEATURE_LEVEL = DWORD