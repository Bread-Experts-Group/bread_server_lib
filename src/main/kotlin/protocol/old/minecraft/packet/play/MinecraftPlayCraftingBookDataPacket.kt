package org.bread_experts_group.protocol.old.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

sealed class MinecraftPlayCraftingBookDataPacket(
	val dataType: MinecraftCraftingBookDataType,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.CRAFTING_BOOK_DATA, data) {
	override fun toString(): String = super.toString() + "[$dataType]"
}