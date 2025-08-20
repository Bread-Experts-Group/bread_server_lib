package org.bread_experts_group.protocol.old.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

sealed class MinecraftPlayAdvancementTabPacket(
	val action: MinecraftAdvancementTabAction,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.ADVANCEMENT_TAB, data) {
	override fun toString(): String = super.toString() + "[$action]"
}