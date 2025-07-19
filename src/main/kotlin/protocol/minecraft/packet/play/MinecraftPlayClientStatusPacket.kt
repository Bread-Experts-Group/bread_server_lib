package org.bread_experts_group.protocol.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayClientStatusPacket(
	val action: MinecraftClientStatusAction,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.CLIENT_STATUS, data) {
	override fun toString(): String = super.toString() + "[$action]"
}