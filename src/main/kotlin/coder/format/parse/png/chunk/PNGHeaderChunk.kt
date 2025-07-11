package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGCompressionType
import org.bread_experts_group.coder.format.parse.png.PNGFilterType
import org.bread_experts_group.coder.format.parse.png.PNGHeaderFlags
import org.bread_experts_group.coder.format.parse.png.PNGInterlaceType

class PNGHeaderChunk(
	val width: Int,
	val height: Int,
	val bitDepth: Int,
	val flags: Set<PNGHeaderFlags>,
	val compressionType: PNGCompressionType,
	val filterType: PNGFilterType,
	val interlaceType: PNGInterlaceType
) : PNGChunk("IHDR", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[$width x $height ($bitDepth-bit), " +
			"$compressionType, $filterType, $interlaceType, " +
			"[" + flags.joinToString(",") + "]]"
}