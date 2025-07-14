package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGPixelDimensions
import java.nio.channels.SeekableByteChannel

class PNGPhysicalPixelDimensionsChunk(
	val x: Int,
	val y: Int,
	val unit: PNGPixelDimensions,
	window: SeekableByteChannel
) : PNGChunk("pHYs", window) {
	override fun toString(): String = super.toString() + "[$x x $y / $unit]"
}