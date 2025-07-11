package org.bread_experts_group.coder.format.parse.png.chunk

import java.awt.Color

class PNGBackgroundChunk(val color: Color) : PNGChunk("bKGD", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[$color]"
}