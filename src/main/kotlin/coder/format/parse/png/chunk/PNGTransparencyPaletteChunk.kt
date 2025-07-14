package org.bread_experts_group.coder.format.parse.png.chunk

import java.nio.channels.SeekableByteChannel

class PNGTransparencyPaletteChunk(
	val alphas: List<Float>,
	window: SeekableByteChannel
) : PNGChunk("tRNS", window), Iterable<Float> {
	override fun iterator(): Iterator<Float> = alphas.iterator()
	override fun toString(): String = super.toString() + alphas.toString()
}