package org.bread_experts_group.ffi.windows.directx

import org.bread_experts_group.generic.Mappable
import java.lang.foreign.ValueLayout

enum class D3DFeatureLevel(
	override val id: UInt,
	override val tag: String
) : Mappable<D3DFeatureLevel, UInt> {
	D3D_FEATURE_LEVEL_1_0_CORE(0x1000u, "Direct3D 9.1 Core"),
	D3D_FEATURE_LEVEL_9_1(0x9100u, "Direct3D 9.1"),
	D3D_FEATURE_LEVEL_9_2(0x9200u, "Direct3D 9.2"),
	D3D_FEATURE_LEVEL_9_3(0x9300u, "Direct3D 9.3"),
	D3D_FEATURE_LEVEL_10_0(0xA000u, "Direct3D 10.0"),
	D3D_FEATURE_LEVEL_10_1(0xA100u, "Direct3D 10.1"),
	D3D_FEATURE_LEVEL_11_0(0xB000u, "Direct3D 11.0"),
	D3D_FEATURE_LEVEL_11_1(0xB100u, "Direct3D 11.1"),
	D3D_FEATURE_LEVEL_12_0(0xC000u, "Direct3D 12.0"),
	D3D_FEATURE_LEVEL_12_1(0xC100u, "Direct3D 12.1"),
	D3D_FEATURE_LEVEL_12_2(0xC200u, "Direct3D 12.2");

	override fun toString(): String = stringForm()
}

val D3D_FEATURE_LEVEL: ValueLayout.OfInt = ValueLayout.JAVA_INT