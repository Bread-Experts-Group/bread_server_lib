package org.bread_experts_group.coder.format.png.chunk

import java.awt.Color

class PNGPaletteChunk(
	val colors: List<Color>
) : PNGChunk("PLTE", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[${colors.size} colors]"
}