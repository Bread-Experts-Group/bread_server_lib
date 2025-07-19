package org.bread_experts_group.coder.format.parse.nbt.tag

class NBTShortTag(val short: Short) : NBTTag(NBTTagType.SHORT) {
	override fun toString(): String = super.toString() + "[$short]"
}