package org.bread_experts_group.protocol.old.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayCraftingBookStatusPacket(
	val open: Boolean,
	val filter: Boolean,
	data: ReadableByteChannel
) : MinecraftPlayCraftingBookDataPacket(MinecraftCraftingBookDataType.STATUS, data) {
	override fun toString(): String = super.toString() + '[' + buildList {
		if (open) add("OPEN")
		if (filter) add("FILTERED")
	}.joinToString(",") + ']'
}