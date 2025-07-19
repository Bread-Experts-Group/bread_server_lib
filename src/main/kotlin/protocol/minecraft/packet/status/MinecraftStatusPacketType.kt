package org.bread_experts_group.protocol.minecraft.packet.status

import org.bread_experts_group.coder.Mappable

enum class MinecraftStatusPacketType(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftStatusPacketType, Int> {
	REQUEST(0x00, "Status Request"),
	PING(0x01, "Ping");

	override fun toString(): String = stringForm()
}