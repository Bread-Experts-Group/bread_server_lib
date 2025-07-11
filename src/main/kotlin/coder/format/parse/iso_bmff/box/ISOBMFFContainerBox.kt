package org.bread_experts_group.coder.format.parse.iso_bmff.box

import org.bread_experts_group.coder.CodingException
import org.bread_experts_group.coder.LazyPartialResult
import org.bread_experts_group.coder.format.parse.iso_bmff.ISOBMFFParser
import java.io.OutputStream

open class ISOBMFFContainerBox(
	tag: String,
	private val boxes: ISOBMFFParser
) : ISOBMFFBox(tag, byteArrayOf()), Iterable<LazyPartialResult<ISOBMFFBox, CodingException>> {
	override fun iterator(): Iterator<LazyPartialResult<ISOBMFFBox, CodingException>> = boxes.iterator()
	override fun toString(): String = "ISOBMFFContainerBox.\"$tag\""

	override fun computeSize(): Long = boxes.sumOf { it.resultSafe.computeSize() }
	override fun write(stream: OutputStream) {
		super.write(stream)
		boxes.forEach { it.resultSafe.write(stream) }
	}
}