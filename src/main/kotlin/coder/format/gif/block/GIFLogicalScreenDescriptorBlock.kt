package org.bread_experts_group.coder.format.gif.block

import java.awt.Color

class GIFLogicalScreenDescriptorBlock(
	val width: Int,
	val height: Int,
	val backgroundColor: Color?,
	val bitDepth: Int,
	val colorsSorted: Boolean,
	val pixelAspectRatio: Float?,
	val colors: List<Color>?
) : GIFBlock(0, byteArrayOf()) {
	override fun toString(): String = "GIFLogicalScreenDescriptorBlock[" + buildList {
		add("$width x $height ($bitDepth-bit)")
		if (backgroundColor != null) add("Global Color Table present [BCKG: $backgroundColor]")
		if (colorsSorted) add("colors sorted")
		if (pixelAspectRatio != null) "pixel ratio: $pixelAspectRatio"
		if (colors != null) "${colors.size} color(s)"
	}.joinToString(", ") + "]"
}