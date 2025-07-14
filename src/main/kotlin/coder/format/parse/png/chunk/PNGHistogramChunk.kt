package org.bread_experts_group.coder.format.parse.png.chunk

import java.nio.channels.SeekableByteChannel

class PNGHistogramChunk(
	val histogram: List<Int>,
	window: SeekableByteChannel
) : PNGChunk("hIST", window), Iterable<Int> {
	override fun iterator(): Iterator<Int> = histogram.iterator()
	override fun toString(): String = super.toString() + "[${histogram.size} histogram entries]"
}