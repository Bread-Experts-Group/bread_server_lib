package org.bread_experts_group.protocol.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayCraftingBookDisplayPacket(
	val id: Int,
	data: ReadableByteChannel
) : MinecraftPlayCraftingBookDataPacket(MinecraftCraftingBookDataType.DISPLAYED_RECIPE, data) {
	override fun toString(): String = super.toString() + "[$id]"
}