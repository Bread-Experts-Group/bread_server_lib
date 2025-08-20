package org.bread_experts_group.protocol.old.minecraft.packet.play

import java.nio.channels.ReadableByteChannel
import java.util.*

class MinecraftPlayPlayerAbilitiesToServerPacket(
	val abilities: EnumSet<MinecraftPlayerAbility>,
	val flightSpeed: Float,
	val walkSpeed: Float,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.PLAYER_ABILITIES_TS, data) {
	override fun toString(): String = super.toString() + "[Flight / Walk Speed: $flightSpeed / $walkSpeed, $abilities]"
}