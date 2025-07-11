package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGPixelDimensions

class PNGPhysicalPixelDimensionsChunk(
	val x: Int,
	val y: Int,
	val unit: PNGPixelDimensions
) : PNGChunk("pHYs", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[$unit, $x x $y]"
}