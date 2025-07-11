package org.bread_experts_group.coder.format.parse.png.chunk

class PNGTextChunk(
	val keyword: String,
	val text: String
) : PNGChunk("tEXt", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[\"$keyword\": \"$text\"]"
}