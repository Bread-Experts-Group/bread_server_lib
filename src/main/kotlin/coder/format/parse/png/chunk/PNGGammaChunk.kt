package org.bread_experts_group.coder.format.parse.png.chunk

class PNGGammaChunk(
	val gamma: Double
) : PNGChunk("gAMA", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[$gamma]"
}