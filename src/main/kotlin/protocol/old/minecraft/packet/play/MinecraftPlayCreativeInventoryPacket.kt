package org.bread_experts_group.protocol.old.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayCreativeInventoryPacket(
	val slot: Int,
	val item: MinecraftSlot,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.CREATIVE_INVENTORY, data) {
	override fun toString(): String = super.toString() + "[#$slot: $item]"
}