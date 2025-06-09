package org.bread_experts_group.coder.format.iso_bmff.box

import java.io.OutputStream

class ISOBMFFContainerBox(
	tag: String,
	val boxes: List<ISOBMFFBox>
) : ISOBMFFBox(tag, byteArrayOf()) {
	override fun toString(): String = "ISOBMFFContainerBox.\"$tag\"[${boxes.size}][\n" +
			boxes.joinToString(",\n") { it.toString().replace("\n", "\n\t") } + "\n]"

	override fun computeSize(): Long = boxes.sumOf { it.computeSize() }
	override fun write(stream: OutputStream) {
		super.write(stream)
		boxes.forEach { it.write(stream) }
	}
}