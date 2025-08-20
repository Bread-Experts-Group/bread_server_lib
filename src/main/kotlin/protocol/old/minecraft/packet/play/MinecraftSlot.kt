package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.coder.format.parse.CodingPartialResult
import org.bread_experts_group.coder.format.parse.nbt.tag.NBTTag

data class MinecraftSlot(
	val itemID: Short,
	val itemCount: Byte?,
	val itemDamage: Short?,
	val nbt: List<CodingPartialResult<NBTTag>>?
)