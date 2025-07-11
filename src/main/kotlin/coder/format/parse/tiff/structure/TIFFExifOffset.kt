package org.bread_experts_group.coder.format.parse.tiff.structure

import org.bread_experts_group.coder.format.parse.CodingPartialResult
import org.bread_experts_group.coder.format.parse.tiff.TIFFDataType
import org.bread_experts_group.coder.format.parse.tiff.TIFFParser
import org.bread_experts_group.coder.format.parse.tiff.TIFFStructureIdentifier

class TIFFExifOffset(
	val tiff: TIFFParser,
	items: Long, offset: Long, location: Long
) : TIFFStructure(
	TIFFStructureIdentifier.EXIF_OFFSET, TIFFDataType.LONG,
	items, offset, location
), Iterable<CodingPartialResult<TIFFStructure>> {
	override fun iterator(): Iterator<CodingPartialResult<TIFFStructure>> = tiff.iterator()
}