package org.bread_experts_group.coder.format.iso_bmff.box

class ISOBMFFContainerBox(
	name: String,
	val boxes: List<ISOBMFFBox>
) : ISOBMFFBox(name, byteArrayOf()) {
	override fun toString(): String = "ContainerBox.\"$name\"[${boxes.size}][\n" +
			boxes.joinToString(",\n") { it.toString().replace("\n", "\n\t") } + "\n]"
}