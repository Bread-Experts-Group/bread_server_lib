package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGCompressionType
import java.nio.channels.SeekableByteChannel

class PNGCompressedTextChunk(
	val keyword: String,
	val compressionMethod: PNGCompressionType,
	val text: String,
	window: SeekableByteChannel
) : PNGChunk("zTXt", window) {
	override fun toString(): String = super.toString() + "[\"$keyword\": \"$text\" ($compressionMethod)]"
}