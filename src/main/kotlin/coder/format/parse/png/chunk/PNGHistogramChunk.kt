package org.bread_experts_group.coder.format.parse.png.chunk

class PNGHistogramChunk(
	val histogram: List<Int>
) : PNGChunk("hIST", byteArrayOf()), Iterable<Int> {
	override fun iterator(): Iterator<Int> = histogram.iterator()
	override fun toString(): String = super.toString() + "[${histogram.size} histogram entries]"
}