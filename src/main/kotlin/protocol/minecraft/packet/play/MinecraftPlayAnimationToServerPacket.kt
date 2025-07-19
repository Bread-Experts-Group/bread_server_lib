package org.bread_experts_group.protocol.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayAnimationToServerPacket(
	val hand: MinecraftHand,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.ANIMATION_TS, data) {
	override fun toString(): String = super.toString() + "[$hand]"
}