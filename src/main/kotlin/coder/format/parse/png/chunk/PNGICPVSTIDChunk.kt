package org.bread_experts_group.coder.format.parse.png.chunk

class PNGICPVSTIDChunk(
	val colorPrimaries: Int,
	val transferFunction: Int,
	val matrixCoefficients: Int,
	val videoFullRange: Int
) : PNGChunk("cICP", byteArrayOf()) {
	override fun toString(): String = super.toString()
}