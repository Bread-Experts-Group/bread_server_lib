package org.bread_experts_group.coder.format.parse.nbt.tag

import org.bread_experts_group.stream.Writable

class NBTListTag(
	val ofType: NBTTagType,
	val tags: List<NBTTag>
) : NBTTag(NBTTagType.LIST), List<NBTTag>, Writable {
	override val size: Int = tags.size
	override fun isEmpty(): Boolean = tags.isEmpty()
	override fun contains(element: NBTTag): Boolean = tags.contains(element)
	override fun iterator(): Iterator<NBTTag> = tags.iterator()
	override fun containsAll(elements: Collection<NBTTag>): Boolean = tags.containsAll(elements)
	override fun get(index: Int): NBTTag = tags[index]
	override fun indexOf(element: NBTTag): Int = tags.indexOf(element)
	override fun lastIndexOf(element: NBTTag): Int = tags.lastIndexOf(element)
	override fun listIterator(): ListIterator<NBTTag> = tags.listIterator()
	override fun listIterator(index: Int): ListIterator<NBTTag> = tags.listIterator(index)
	override fun subList(
		fromIndex: Int,
		toIndex: Int
	): List<NBTTag> = tags.subList(fromIndex, toIndex)

	override fun toString(): String = super<NBTTag>.toString() + "[$ofType: $tags]"
}