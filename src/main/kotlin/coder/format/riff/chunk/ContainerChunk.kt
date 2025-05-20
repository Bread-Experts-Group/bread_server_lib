package org.bread_experts_group.coder.format.riff.chunk

class ContainerChunk(
	identifier: String,
	val localIdentifier: String,
	val chunks: Array<RIFFChunk>
) : RIFFChunk(identifier, byteArrayOf())