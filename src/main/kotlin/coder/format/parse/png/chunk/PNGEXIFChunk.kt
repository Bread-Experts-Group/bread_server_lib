package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.CodingPartialResult
import org.bread_experts_group.coder.format.parse.tiff.TIFFParser
import org.bread_experts_group.coder.format.parse.tiff.structure.TIFFStructure

class PNGEXIFChunk(
	val exif: TIFFParser
) : PNGChunk("eXIf", byteArrayOf()), Iterable<CodingPartialResult<TIFFStructure>> {
	override fun iterator(): Iterator<CodingPartialResult<TIFFStructure>> = exif.iterator()
}