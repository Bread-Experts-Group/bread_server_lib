package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.channel.ByteArrayChannel
import java.util.*

class MinecraftPlayPlayerListRemovePacket(
	players: List<UUID>
) : MinecraftPlayUpdatePlayerListPacket(
	MinecraftPlayerListAction.REMOVE,
	players.associateWith { _ -> ByteArrayChannel(byteArrayOf()) }
)