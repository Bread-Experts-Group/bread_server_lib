package org.bread_experts_group.api.graphics.feature.window.icon

data class Image2D(
	override val planes: Array<ImagePlane>,
	val width: UInt,
	val height: UInt
) : Image, GraphicsIcon