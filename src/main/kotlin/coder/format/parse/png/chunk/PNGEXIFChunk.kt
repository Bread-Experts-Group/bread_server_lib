package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.ByteParser
import org.bread_experts_group.coder.format.parse.CodingPartialResult
import org.bread_experts_group.coder.format.parse.tiff.TIFFStructureIdentifier
import org.bread_experts_group.coder.format.parse.tiff.structure.TIFFStructure
import java.nio.channels.SeekableByteChannel

class PNGEXIFChunk(
	val exif: ByteParser<TIFFStructureIdentifier, TIFFStructure, SeekableByteChannel>,
	window: SeekableByteChannel
) : PNGChunk("eXIf", window), Iterable<CodingPartialResult<TIFFStructure>> {
	override fun iterator(): Iterator<CodingPartialResult<TIFFStructure>> = exif.iterator()
}