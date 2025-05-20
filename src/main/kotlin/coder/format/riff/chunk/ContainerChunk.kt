package org.bread_experts_group.coder.format.riff.chunk

data class ContainerChunk(
	override val identifier: String,
	val localIdentifier: String,
	val chunks: List<RIFFChunk>
) : RIFFChunk(identifier, byteArrayOf()) {
	override fun toString(): String = "ContainerChunk.$identifier[$localIdentifier:[${chunks.size}]$chunks]"
}