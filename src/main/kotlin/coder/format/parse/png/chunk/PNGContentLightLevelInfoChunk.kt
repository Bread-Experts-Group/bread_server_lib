package org.bread_experts_group.coder.format.parse.png.chunk

import java.nio.channels.SeekableByteChannel

class PNGContentLightLevelInfoChunk(
	val maximumLightLevel: Double,
	val maximumFrameAverageLightLevel: Double,
	window: SeekableByteChannel
) : PNGChunk("cLLI", window) {
	override fun toString(): String = super.toString() + "[MaxCLL: $maximumLightLevel, " +
			"MaxFALL: $maximumFrameAverageLightLevel]"
}