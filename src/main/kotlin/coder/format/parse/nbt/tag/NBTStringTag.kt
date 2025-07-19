package org.bread_experts_group.coder.format.parse.nbt.tag

class NBTStringTag(val string: String) : NBTTag(NBTTagType.UTF_8) {
	override fun toString(): String = super.toString() + "[\"$string\"]"
}