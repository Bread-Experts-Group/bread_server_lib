package org.bread_experts_group.protocol.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayPlayerLookPacket(
	val yaw: Float,
	val pitch: Float,
	val grounded: Boolean,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.PLAYER_LOOK, data) {
	override fun toString(): String = super.toString() + "[$yaw*, $pitch*${if (grounded) " / grounded" else ""}]"
}