package org.bread_experts_group.coder.format.parse.tiff.structure

import org.bread_experts_group.coder.format.parse.tiff.TIFFDataType
import org.bread_experts_group.coder.format.parse.tiff.TIFFStructureIdentifier

class TIFFTextStructure(
	identifier: TIFFStructureIdentifier,
	items: Long,
	offset: Long,
	location: Long,
	val text: String
) : TIFFStructure(identifier, TIFFDataType.STRING, items, offset, location) {
	override fun toString(): String = super.toString() + "[\"$text\"]"
}