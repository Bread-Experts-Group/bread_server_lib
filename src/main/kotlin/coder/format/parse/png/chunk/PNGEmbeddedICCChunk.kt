package org.bread_experts_group.coder.format.parse.png.chunk

class PNGEmbeddedICCChunk(
	val profileName: String,
	val profileData: ByteArray
) : PNGChunk("iCCP", byteArrayOf()) {
	override fun toString(): String = super.toString() + "[\"$profileName\": #${profileData.size}]"
}