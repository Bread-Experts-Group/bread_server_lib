package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.numeric.geometry.Point2D
import java.nio.channels.SeekableByteChannel

class PNGChromaticitiesChunk(
	val white: Point2D,
	val red: Point2D,
	val green: Point2D,
	val blue: Point2D,
	window: SeekableByteChannel
) : PNGChunk("cHRM", window) {
	override fun toString(): String = super.toString() + "[W: $white, R: $red, G: $green, B: $blue]"
}