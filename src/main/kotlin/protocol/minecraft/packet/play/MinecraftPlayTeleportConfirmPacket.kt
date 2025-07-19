package org.bread_experts_group.protocol.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayTeleportConfirmPacket(
	val id: Int,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.TELEPORT_CONFIRM, data) {
	override fun toString(): String = super.toString() + "[#$id]"
}