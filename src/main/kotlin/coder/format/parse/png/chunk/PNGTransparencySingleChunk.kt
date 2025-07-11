package org.bread_experts_group.coder.format.parse.png.chunk

import java.awt.Color

class PNGTransparencySingleChunk(
	val sample: Color
) : PNGChunk("tRNS", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[$sample]"
}