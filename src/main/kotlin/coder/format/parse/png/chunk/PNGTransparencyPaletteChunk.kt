package org.bread_experts_group.coder.format.parse.png.chunk

class PNGTransparencyPaletteChunk(
	val alphas: List<Float>
) : PNGChunk("tRNS", byteArrayOf()), Iterable<Float> {
	override fun iterator(): Iterator<Float> = alphas.iterator()
	override fun toString(): String = super.toString() + alphas.toString()
}