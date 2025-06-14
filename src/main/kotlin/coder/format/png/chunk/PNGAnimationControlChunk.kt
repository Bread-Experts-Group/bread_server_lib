package org.bread_experts_group.coder.format.png.chunk

class PNGAnimationControlChunk(
	val frames: Int,
	val loopCount: Int
) : PNGChunk("acTL", byteArrayOf()) {
	override fun toString(): String = super.toString() +
			"[$frames frames${if (loopCount > 0) ", $loopCount loops" else ""}]"
}