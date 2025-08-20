package org.bread_experts_group.protocol.old.minecraft.packet.status

import org.bread_experts_group.channel.ByteArrayChannel
import org.bread_experts_group.channel.bytes

class MinecraftStatusPongPacket(
	val long: Long
) : MinecraftStatusPacket(
	MinecraftStatusPacketType.PING,
	ByteArrayChannel(long.bytes())
) {
	override fun toString(): String = super.toString() + "[$long]"
	override fun computeSize(): Int = 8
}