package org.bread_experts_group.coder.format.parse.png.chunk

class PNGMasteringDisplayColorVolumeChunk(
	val colorPrimaryChromaticities: ByteArray,
	val whitePointChromaticity: Double,
	val maximumLuminance: Double,
	val minimumLuminance: Double
) : PNGChunk("mDCV", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[TODO, MDCV]"
}