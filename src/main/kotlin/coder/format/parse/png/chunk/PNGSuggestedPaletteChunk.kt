package org.bread_experts_group.coder.format.parse.png.chunk

import java.awt.Color
import java.nio.channels.SeekableByteChannel

class PNGSuggestedPaletteChunk(
	val name: String,
	val colors: List<Pair<Color, Int>>,
	window: SeekableByteChannel
) : PNGChunk("sPLT", window), Iterable<Pair<Color, Int>> {
	override fun iterator(): Iterator<Pair<Color, Int>> = colors.iterator()
	override fun toString(): String = super.toString() + "[\"$name\", ${colors.size} colors]"
}