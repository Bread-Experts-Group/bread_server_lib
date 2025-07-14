package org.bread_experts_group.coder.format.parse.png.chunk

import java.awt.Color
import java.nio.channels.SeekableByteChannel

class PNGTransparencySingleChunk(
	val sample: Color,
	window: SeekableByteChannel
) : PNGChunk("tRNS", window) {
	override fun toString(): String = super.toString() + "[$sample]"
}