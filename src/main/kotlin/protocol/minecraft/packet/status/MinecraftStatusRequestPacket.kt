package org.bread_experts_group.protocol.minecraft.packet.status

import java.nio.channels.ReadableByteChannel

class MinecraftStatusRequestPacket(
	data: ReadableByteChannel
) : MinecraftStatusPacket(MinecraftStatusPacketType.REQUEST, data)