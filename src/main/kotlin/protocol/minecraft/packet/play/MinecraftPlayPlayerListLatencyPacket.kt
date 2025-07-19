package org.bread_experts_group.protocol.minecraft.packet.play

import org.bread_experts_group.channel.ByteArrayChannel
import org.bread_experts_group.protocol.minecraft.varNi
import java.util.*

class MinecraftPlayPlayerListLatencyPacket(
	players: Map<UUID, Int>
) : MinecraftPlayUpdatePlayerListPacket(
	MinecraftPlayerListAction.UPDATE_PING,
	players.mapValues { (_, entry) -> ByteArrayChannel(varNi(entry)) }
)