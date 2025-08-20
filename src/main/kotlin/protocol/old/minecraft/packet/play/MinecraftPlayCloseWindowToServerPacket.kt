package org.bread_experts_group.protocol.old.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayCloseWindowToServerPacket(
	val id: Byte,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.CLOSE_WINDOW_TS, data) {
	override fun toString(): String = super.toString() + "[$id]"
}