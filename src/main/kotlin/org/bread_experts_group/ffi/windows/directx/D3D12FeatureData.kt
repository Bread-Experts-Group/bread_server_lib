package org.bread_experts_group.ffi.windows.directx

abstract class D3D12FeatureData(val feature: D3D12Feature) {
	final override fun toString(): String = "=== $feature\n${expandToString()}"
	abstract fun expandToString(): String
}