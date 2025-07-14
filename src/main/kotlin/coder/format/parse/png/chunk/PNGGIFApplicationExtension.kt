package org.bread_experts_group.coder.format.parse.png.chunk

import java.nio.channels.SeekableByteChannel

class PNGGIFApplicationExtension(
	val identifier: String,
	val code: String,
	window: SeekableByteChannel
) : PNGChunk("gIFx", window) {
	override fun toString(): String = super.toString() + "[\"$identifier\":\"$code\"]"
}