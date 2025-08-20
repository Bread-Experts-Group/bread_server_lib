package org.bread_experts_group.protocol.old.minecraft.packet.handshake

import java.nio.channels.ReadableByteChannel

class MinecraftHandshakeInitiatePacket(
	val protocol: Int,
	val serverAddress: String,
	val port: UShort,
	val nextState: MinecraftHandshakeNextState,
	channel: ReadableByteChannel
) : MinecraftHandshakePacket(MinecraftHandshakePacketType.HANDSHAKE, channel) {
	override fun toString(): String = super.toString() + "[$protocol @ $serverAddress:$port -> $nextState]"
}