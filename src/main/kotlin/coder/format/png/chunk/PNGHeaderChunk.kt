package org.bread_experts_group.taggart.png.chunk

import org.bread_experts_group.taggart.png.PNGCompressionType
import org.bread_experts_group.taggart.png.PNGFilterType
import org.bread_experts_group.taggart.png.PNGInterlaceType

class PNGHeaderChunk(
	val width: Int,
	val height: Int,
	val bitDepth: Int,
	val palette: Boolean,
	val color: Boolean,
	val alpha: Boolean,
	val compressionType: PNGCompressionType,
	val filterType: PNGFilterType,
	val interlaceType: PNGInterlaceType
) : PNGChunk("IHDR", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[$width x $height ($bitDepth-bit), " +
			"$compressionType, filter: $filterType, interlace: $interlaceType, [" + buildList {
		if (palette) add("PALETTE")
		if (color) add("TRUE-COLOR")
		if (alpha) add("ALPHA")
	}.joinToString(", ") + "]]"
}