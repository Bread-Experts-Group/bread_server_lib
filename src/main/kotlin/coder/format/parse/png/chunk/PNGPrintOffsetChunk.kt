package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGPrintOffsetUnit
import java.nio.channels.SeekableByteChannel

class PNGPrintOffsetChunk(
	val x: Long,
	val y: Long,
	val unit: PNGPrintOffsetUnit,
	window: SeekableByteChannel
) : PNGChunk("oFFs", window) {
	override fun toString(): String = super.toString() + "[$x x $y $unit]"
}