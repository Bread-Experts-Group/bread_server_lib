package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGSRGBIntent
import java.nio.channels.SeekableByteChannel

class PNGSRGBChunk(
	val intent: PNGSRGBIntent,
	window: SeekableByteChannel
) : PNGChunk("sRGB", window) {
	override fun toString(): String = super.toString() + "[$intent]"
}