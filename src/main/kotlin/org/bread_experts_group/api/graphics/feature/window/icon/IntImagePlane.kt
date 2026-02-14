package org.bread_experts_group.api.graphics.feature.window.icon

data class IntImagePlane(
	override val type: ImagePlaneType,
	val data: IntArray
) : ImagePlane