package org.bread_experts_group.protocol.old.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayKeepAliveReplyPacket(
	val long: Long,
	data: ReadableByteChannel
) : MinecraftPlayPacket(
	MinecraftPlayPacketType.KEEP_ALIVE_TS,
	data
) {
	override fun toString(): String = super.toString() + "[$long]"
}