package org.bread_experts_group.coder.format.parse.png.chunk

import java.nio.channels.SeekableByteChannel

class PNGDataChunk(
	val windows: List<SeekableByteChannel>
) : PNGChunk("IDAT", windows.first()) { // TODO!!!!!!! Seekable Consolidated Byte Channel!
	override fun computeSize(): Long = windows.sumOf { it.size() }
}