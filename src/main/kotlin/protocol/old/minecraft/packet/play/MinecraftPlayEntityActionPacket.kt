package org.bread_experts_group.protocol.old.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayEntityActionPacket(
	val entityID: Int,
	val action: MinecraftEntityAction,
	val jumpBoost: Int,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.ENTITY_ACTION, data) {
	override fun toString(): String = "[#$entityID, $action [$jumpBoost]]"
}