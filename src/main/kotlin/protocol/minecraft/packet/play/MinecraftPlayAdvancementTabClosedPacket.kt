package org.bread_experts_group.protocol.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayAdvancementTabClosedPacket(
	data: ReadableByteChannel
) : MinecraftPlayAdvancementTabPacket(
	MinecraftAdvancementTabAction.CLOSED,
	data
)