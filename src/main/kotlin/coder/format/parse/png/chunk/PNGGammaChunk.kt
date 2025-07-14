package org.bread_experts_group.coder.format.parse.png.chunk

import java.nio.channels.SeekableByteChannel

class PNGGammaChunk(
	val gamma: Double,
	window: SeekableByteChannel
) : PNGChunk("gAMA", window) {
	override fun toString(): String = super.toString() + "[$gamma]"
}