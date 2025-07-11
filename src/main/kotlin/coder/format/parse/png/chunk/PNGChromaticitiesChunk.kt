package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.Point2D

class PNGChromaticitiesChunk(
	val white: Point2D,
	val red: Point2D,
	val green: Point2D,
	val blue: Point2D
) : PNGChunk("cHRM", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[W: $white, R: $red, G: $green, B: $blue]"
}