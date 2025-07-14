package org.bread_experts_group.coder.format.parse.png.chunk

import java.nio.channels.SeekableByteChannel

class PNGFrameDataChunk(
	override val sequence: Int,
	val windows: List<SeekableByteChannel>
) : PNGChunk("fdAT", windows.first()), PNGSequencedChunk {
	override fun computeSize(): Long = windows.sumOf { it.size() }
	override fun toString(): String = super.toString() + "[#$sequence]"
} // TODO!!!!!!! Seekable Consolidated Byte Channel!