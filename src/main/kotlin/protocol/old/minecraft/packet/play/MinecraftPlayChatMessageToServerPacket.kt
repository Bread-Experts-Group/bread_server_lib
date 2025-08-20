package org.bread_experts_group.protocol.old.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayChatMessageToServerPacket(
	val message: String,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.CHAT_MESSAGE_TS, data) {
	override fun toString(): String = super.toString() + "[\"$message\"]"
}