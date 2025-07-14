package org.bread_experts_group.coder.format.parse.png.chunk

import java.nio.channels.SeekableByteChannel

class PNGAnimationControlChunk(
	val frames: Int,
	val loopCount: Int,
	window: SeekableByteChannel
) : PNGChunk("acTL", window) {
	override fun toString(): String = super.toString() +
			"[$frames frames${if (loopCount > 0) ", $loopCount loops" else ""}]"
}