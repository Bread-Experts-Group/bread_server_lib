package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.Flaggable
import java.lang.foreign.ValueLayout

enum class D3D12ShaderMinPrecisionSupport(override val position: Long) : Flaggable {
	D3D12_SHADER_MIN_PRECISION_SUPPORT_NONE(0x0),
	D3D12_SHADER_MIN_PRECISION_SUPPORT_10_BIT(0x1),
	D3D12_SHADER_MIN_PRECISION_SUPPORT_16_BIT(0x2)
}

val D3D12_SHADER_MIN_PRECISION_SUPPORT: ValueLayout.OfInt = ValueLayout.JAVA_INT