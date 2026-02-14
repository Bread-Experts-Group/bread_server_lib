package org.bread_experts_group.api.graphics.feature.window.icon.compile

data class CompiledIntPlane32(
	val wordShift: Int,
	val multiplicand: Int,
	val sampleMask: Int,
	val wordMask: Int,
	val wordMaskShift: Int,
	val data: IntArray
)