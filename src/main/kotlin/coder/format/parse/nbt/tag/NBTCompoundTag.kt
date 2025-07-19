package org.bread_experts_group.coder.format.parse.nbt.tag

import org.bread_experts_group.stream.Writable

class NBTCompoundTag(
	val tags: Map<String, NBTTag>
) : NBTTag(NBTTagType.COMPOUND), Map<String, NBTTag>, Writable {
	override val size: Int = tags.size
	override val keys: Set<String> = tags.keys
	override val values: Collection<NBTTag> = tags.values
	override val entries: Set<Map.Entry<String, NBTTag>> = tags.entries

	override fun isEmpty(): Boolean = tags.isEmpty()
	override fun containsKey(key: String): Boolean = tags.containsKey(key)
	override fun containsValue(value: NBTTag): Boolean = tags.containsValue(value)
	override fun get(key: String): NBTTag? = tags[key]
	override fun toString(): String = super<NBTTag>.toString() + '[' + tags.map {
		"\"${it.key}\": ${it.value}"
	}.joinToString(", ") + ']'
}