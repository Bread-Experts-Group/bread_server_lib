package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGCompressionType
import java.util.*

class PNGInternationalTextChunk(
	val keyword: String,
	val compressionMethod: PNGCompressionType?,
	val language: Locale,
	val displayKeyword: String,
	val displayText: String
) : PNGChunk("iTXt", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[\"$keyword\", $language: " +
			"\"$displayKeyword\": \"$displayText\"${if (compressionMethod != null) " ($compressionMethod)" else ""}]"
}