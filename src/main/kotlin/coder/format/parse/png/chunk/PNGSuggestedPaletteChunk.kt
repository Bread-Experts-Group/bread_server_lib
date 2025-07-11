package org.bread_experts_group.coder.format.parse.png.chunk

import java.awt.Color

class PNGSuggestedPaletteChunk(
	val name: String,
	val colors: List<Pair<Color, Int>>
) : PNGChunk("sPLT", byteArrayOf()), Iterable<Pair<Color, Int>> {
	override fun iterator(): Iterator<Pair<Color, Int>> = colors.iterator()
	override fun toString(): String = super.toString() + "[\"$name\", ${colors.size} colors]"
}