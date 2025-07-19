package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.numeric.geometry.Point2D
import java.nio.channels.SeekableByteChannel

class PNGMasteringDisplayColorVolumeChunk(
	val redChromaticity: Point2D,
	val greenChromaticity: Point2D,
	val blueChromaticity: Point2D,
	val whitePointChromaticity: Point2D,
	val maximumLuminance: Double,
	val minimumLuminance: Double,
	window: SeekableByteChannel
) : PNGChunk("mDCV", window) {
	override fun toString(): String = super.toString() + "[R: $redChromaticity, G: $greenChromaticity, B: " +
			"$blueChromaticity, W: $whitePointChromaticity, Lum: $minimumLuminance .. $maximumLuminance]"
}