package org.bread_experts_group.coder.format.parse.png.chunk

import java.awt.Color
import java.nio.channels.SeekableByteChannel

class PNGPaletteChunk(
	val colors: List<Color>,
	window: SeekableByteChannel
) : PNGChunk("PLTE", window), Iterable<Color> {
	override fun iterator(): Iterator<Color> = colors.iterator()
	override fun toString(): String = super.toString() + "[${colors.size} colors]"
}