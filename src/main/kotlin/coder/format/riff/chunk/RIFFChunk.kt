package org.bread_experts_group.coder.format.riff.chunk

import org.bread_experts_group.Writable
import java.io.OutputStream

open class RIFFChunk(
	open val identifier: String,
	val data: ByteArray
) : Writable {
	override fun toString(): String = "RIFFChunk.\"$identifier\"[${data.size}]"
	override fun write(stream: OutputStream) = TODO("RIFFChunk writing")
}