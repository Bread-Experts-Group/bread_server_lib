package org.bread_experts_group.protocol.minecraft.packet.status

import java.nio.channels.ReadableByteChannel

class MinecraftStatusPingPacket(
	val long: Long,
	data: ReadableByteChannel
) : MinecraftStatusPacket(MinecraftStatusPacketType.PING, data) {
	override fun toString(): String = super.toString() + "[$long]"
}