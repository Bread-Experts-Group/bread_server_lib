package org.bread_experts_group.coder.format.riff.chunk

open class RIFFChunk(
	open val identifier: String,
	val data: ByteArray
) {
	override fun toString(): String = "RIFFChunk.$identifier[${data.size}]"
}