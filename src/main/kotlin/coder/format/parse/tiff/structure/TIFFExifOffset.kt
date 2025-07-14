package org.bread_experts_group.coder.format.parse.tiff.structure

import org.bread_experts_group.coder.format.parse.ByteParser
import org.bread_experts_group.coder.format.parse.CodingPartialResult
import org.bread_experts_group.coder.format.parse.tiff.TIFFDataType
import org.bread_experts_group.coder.format.parse.tiff.TIFFStructureIdentifier
import java.nio.channels.SeekableByteChannel

class TIFFExifOffset(
	val tiff: ByteParser<TIFFStructureIdentifier, TIFFStructure, SeekableByteChannel>,
	items: Long, offset: Long, location: Long
) : TIFFStructure(
	TIFFStructureIdentifier.EXIF_OFFSET, TIFFDataType.LONG,
	items, offset, location
), Iterable<CodingPartialResult<TIFFStructure>> {
	override fun iterator(): Iterator<CodingPartialResult<TIFFStructure>> = tiff.iterator()
}