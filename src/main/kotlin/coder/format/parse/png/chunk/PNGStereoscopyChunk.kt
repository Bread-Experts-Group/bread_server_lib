package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGStereoscopyMode
import java.nio.channels.SeekableByteChannel

class PNGStereoscopyChunk(
	val mode: PNGStereoscopyMode,
	window: SeekableByteChannel
) : PNGChunk("sTER", window) {
	override fun toString(): String = super.toString() + "[Mode: $mode]"
}