package org.bread_experts_group.coder.format.parse.png.chunk

import java.time.ZonedDateTime

class PNGTimestampChunk(val time: ZonedDateTime) : PNGChunk("tIME", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[$time]"
}