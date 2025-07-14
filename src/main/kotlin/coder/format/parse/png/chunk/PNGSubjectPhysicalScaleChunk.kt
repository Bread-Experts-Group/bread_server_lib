package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGSubjectPhysicalScaleUnit
import java.math.BigDecimal
import java.nio.channels.SeekableByteChannel

class PNGSubjectPhysicalScaleChunk(
	val unit: PNGSubjectPhysicalScaleUnit,
	val pxWidth: BigDecimal,
	val pxHeight: BigDecimal,
	window: SeekableByteChannel
) : PNGChunk("sCAL", window) {
	override fun toString(): String = super.toString() + "[$pxWidth x $pxHeight $unit]"
}