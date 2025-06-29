package org.bread_experts_group.coder.format.iso_bmff.box

import org.bread_experts_group.coder.format.iso_bmff.ISOBMFFParser
import java.io.OutputStream

open class ISOBMFFContainerBox(
	tag: String,
	private val boxes: ISOBMFFParser
) : ISOBMFFBox(tag, byteArrayOf()), Iterable<ISOBMFFBox> {
	override fun iterator(): Iterator<ISOBMFFBox> = boxes.iterator()
	override fun toString(): String = "ISOBMFFContainerBox.\"$tag\""

	override fun computeSize(): Long = boxes.sumOf { it.computeSize() }
	override fun write(stream: OutputStream) {
		super.write(stream)
		boxes.forEach { it.write(stream) }
	}
}