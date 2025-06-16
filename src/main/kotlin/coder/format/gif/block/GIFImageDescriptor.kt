package org.bread_experts_group.coder.format.gif.block

import org.bread_experts_group.stream.ConsolidatedInputStream
import java.awt.Color

class GIFImageDescriptor(
	val x: Int,
	val y: Int,
	val width: Int,
	val height: Int,
	val interlaced: Boolean,
	val colorsSorted: Boolean,
	val localColors: List<Color>?,
	val lzwMinimumCodeSize: Int,
	val rasterData: ConsolidatedInputStream
) : GIFBlock(0x2C, byteArrayOf()) {
	override fun toString(): String = "GIFImageDescriptor[" + buildList {
		add("$width x $height")
		add("@$x x $y")
		if (interlaced) add("interlaced")
		if (colorsSorted) add("colors sorted")
		if (localColors != null) "${localColors.size} color(s)"
		add("LZW minimum code size: $lzwMinimumCodeSize")
		add("${rasterData.available()} bytes")
	}.joinToString(", ") + "]"
}