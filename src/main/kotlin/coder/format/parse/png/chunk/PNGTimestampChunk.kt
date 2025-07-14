package org.bread_experts_group.coder.format.parse.png.chunk

import java.nio.channels.SeekableByteChannel
import java.time.ZonedDateTime

class PNGTimestampChunk(
	val time: ZonedDateTime,
	window: SeekableByteChannel
) : PNGChunk("tIME", window) {
	override fun toString(): String = super.toString() + "[$time]"
}