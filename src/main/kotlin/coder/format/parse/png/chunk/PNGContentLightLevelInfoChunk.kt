package org.bread_experts_group.coder.format.parse.png.chunk

class PNGContentLightLevelInfoChunk(
	val maximumLightLevel: Double,
	val maximumFrameAverageLightLevel: Double
) : PNGChunk("cLLI", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[MaxCLL: $maximumLightLevel, " +
			"MaxFALL: $maximumFrameAverageLightLevel]"
}