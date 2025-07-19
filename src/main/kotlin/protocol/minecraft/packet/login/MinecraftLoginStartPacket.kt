package org.bread_experts_group.protocol.minecraft.packet.login

import java.nio.channels.ReadableByteChannel

class MinecraftLoginStartPacket(
	val username: String,
	data: ReadableByteChannel
) : MinecraftLoginPacket(MinecraftLoginPacketType.LOGIN_START, data) {
	override fun toString(): String = super.toString() + "[\"${username}\"]"
}