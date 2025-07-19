package org.bread_experts_group.protocol.minecraft.packet.handshake

import org.bread_experts_group.coder.Mappable

enum class MinecraftHandshakePacketType(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftHandshakePacketType, Int> {
	HANDSHAKE(0x00, "Handshake");

	override fun toString(): String = stringForm()
}