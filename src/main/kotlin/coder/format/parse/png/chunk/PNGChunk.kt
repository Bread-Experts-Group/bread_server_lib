package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.OutputStream
import java.nio.channels.SeekableByteChannel

open class PNGChunk(
	override val tag: String,
	val window: SeekableByteChannel
) : Tagged<String>, Writable {
	val critical = tag[0].isUpperCase()
	val specification = tag[1].isUpperCase()
	val reserved = tag[2].isLowerCase()
	val safeToCopy = tag[0].isLowerCase()

	final override fun write(stream: OutputStream) {
		TODO("PNG Writing")
//		stream.write32(data.size)
//		val tagBytes = tag.toByteArray(Charsets.ISO_8859_1)
//		stream.write(tagBytes)
//		stream.write(data)
//		val crc32 = CRC32()
//		crc32.update(tagBytes + data)
//		stream.write32(crc32.value)
	}

	override fun computeSize(): Long = window.size()
	override fun toString(): String = "PNGChunk.\"$tag\"[#${computeSize()}][" +
			(if (critical) "CRITICAL" else "ANCILLARY") +
			(if (specification) ",SPECIFICATION" else ",PRIVATE-USE") +
			(if (reserved) ",RESERVED" else "") +
			(if (safeToCopy) ",SAFE-TO-COPY" else ",UNSAFE-TO-COPY") +
			']'
}