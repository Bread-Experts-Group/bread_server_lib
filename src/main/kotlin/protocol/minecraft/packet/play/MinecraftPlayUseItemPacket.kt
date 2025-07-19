package org.bread_experts_group.protocol.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayUseItemPacket(
	val hand: MinecraftHand,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.USE_ITEM, data) {
	override fun toString(): String = super.toString() + "[$hand]"
}