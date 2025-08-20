package org.bread_experts_group.protocol.old.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayAdvancementTabOpenedPacket(
	val tab: String,
	data: ReadableByteChannel,
) : MinecraftPlayAdvancementTabPacket(
	MinecraftAdvancementTabAction.OPENED,
	data
) {
	override fun toString(): String = super.toString() + "[$tab]"
}