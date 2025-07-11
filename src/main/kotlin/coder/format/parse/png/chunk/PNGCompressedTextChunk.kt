package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGCompressionType

class PNGCompressedTextChunk(
	val keyword: String,
	val compressionMethod: PNGCompressionType,
	val text: String
) : PNGChunk("zTXt", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[\"$keyword\": \"$text\" ($compressionMethod)]"
}