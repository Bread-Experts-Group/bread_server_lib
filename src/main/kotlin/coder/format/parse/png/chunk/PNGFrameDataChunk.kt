package org.bread_experts_group.coder.format.parse.png.chunk

class PNGFrameDataChunk(
	override val sequence: Int,
	override val data: ByteArray
) : PNGChunk("fdAT", data), PNGSequencedChunk {
	override fun toString(): String = super.toString() + "[#$sequence]"
}