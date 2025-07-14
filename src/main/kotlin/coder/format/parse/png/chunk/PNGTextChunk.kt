package org.bread_experts_group.coder.format.parse.png.chunk

import java.nio.channels.SeekableByteChannel

class PNGTextChunk(
	val keyword: String,
	val text: String,
	window: SeekableByteChannel
) : PNGChunk("tEXt", window) {
	override fun toString(): String = super.toString() + "[\"$keyword\": \"$text\"]"
}