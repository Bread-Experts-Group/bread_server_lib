package org.bread_experts_group.coder.format.parse.png.chunk

import java.awt.Color
import java.nio.channels.SeekableByteChannel

class PNGBackgroundChunk(
	val color: Color,
	window: SeekableByteChannel
) : PNGChunk("bKGD", window) {
	override fun toString(): String = super.toString() + "[$color]"
}