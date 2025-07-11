package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGSRGBIntent

class PNGSRGBChunk(val intent: PNGSRGBIntent) : PNGChunk("sRGB", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[$intent]"
}