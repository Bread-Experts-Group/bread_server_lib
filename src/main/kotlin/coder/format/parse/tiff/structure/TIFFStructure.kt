package org.bread_experts_group.coder.format.parse.tiff.structure

import org.bread_experts_group.coder.format.parse.tiff.TIFFDataType
import org.bread_experts_group.coder.format.parse.tiff.TIFFStructureIdentifier
import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.OutputStream

open class TIFFStructure(
	override val tag: TIFFStructureIdentifier,
	val dataType: TIFFDataType,
	val items: Long,
	val offset: Long,
	val location: Long
) : Tagged<TIFFStructureIdentifier>, Writable {
	override fun toString(): String = "TIFFStructure.$tag[$dataType, #$items@$offset]@$location"
	override fun write(stream: OutputStream) {
		TODO("Not yet implemented")
	}
}