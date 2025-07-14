package org.bread_experts_group.coder.format.parse.png.chunk

import java.nio.channels.SeekableByteChannel

class PNGEmbeddedICCChunk(
	val profileName: String,
	val profileData: ByteArray,
	window: SeekableByteChannel
) : PNGChunk("iCCP", window) {
	override fun toString(): String = super.toString() + "[\"$profileName\": #${profileData.size}]"
}