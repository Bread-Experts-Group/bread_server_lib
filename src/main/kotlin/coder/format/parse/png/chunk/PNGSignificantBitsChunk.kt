package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.stream.maskI
import java.nio.channels.SeekableByteChannel

class PNGSignificantBitsChunk(
	val bits: ByteArray,
	window: SeekableByteChannel
) : PNGChunk("sBIT", window) {
	override fun toString(): String = super.toString() + "[${
		bits.joinToString("|") {
			it.toInt().maskI().toUByte().toString(2).padStart(8, '0')
		}
	}]"
}