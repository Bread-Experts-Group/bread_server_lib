package org.bread_experts_group.coder.format.riff.chunk

data class TextChunk(
	override val identifier: String,
	val text: String
) : RIFFChunk(identifier, byteArrayOf()) {
	override fun toString(): String = "RIFFChunk.\"${identifier}\"[\"$text\"]"
}