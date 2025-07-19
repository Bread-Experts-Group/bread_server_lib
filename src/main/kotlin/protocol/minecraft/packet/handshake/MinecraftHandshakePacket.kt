package org.bread_experts_group.protocol.minecraft.packet.handshake

import org.bread_experts_group.protocol.minecraft.packet.MinecraftPacket
import java.nio.channels.ReadableByteChannel

sealed class MinecraftHandshakePacket(
	val type: MinecraftHandshakePacketType,
	data: ReadableByteChannel
) : MinecraftPacket(data) {
	override fun toString(): String = "MinecraftHandshakePacket.$type[$data]"
}