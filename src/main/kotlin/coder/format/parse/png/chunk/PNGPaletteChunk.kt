package org.bread_experts_group.coder.format.parse.png.chunk

import java.awt.Color

class PNGPaletteChunk(
	val colors: List<Color>
) : PNGChunk("PLTE", byteArrayOf()), Iterable<Color> {
	override fun iterator(): Iterator<Color> = colors.iterator()
	override fun toString(): String = super.toString() + "[${colors.size} colors]"
}