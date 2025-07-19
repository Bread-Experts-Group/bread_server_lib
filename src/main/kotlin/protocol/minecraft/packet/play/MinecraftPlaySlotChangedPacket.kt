package org.bread_experts_group.protocol.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlaySlotChangedPacket(
	val slot: UShort,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.SLOT_CHANGED, data) {
	override fun toString(): String = super.toString() + "[#$slot]"
}