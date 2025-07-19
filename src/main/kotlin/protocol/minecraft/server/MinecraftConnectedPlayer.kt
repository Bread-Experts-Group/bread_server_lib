package org.bread_experts_group.protocol.minecraft.server

import org.bread_experts_group.protocol.minecraft.packet.MinecraftPacket
import org.bread_experts_group.protocol.minecraft.packet.play.MinecraftGameMode
import org.bread_experts_group.protocol.minecraft.packet.play.MinecraftPlayerListAddEntry
import org.bread_experts_group.protocol.minecraft.packet.play.MinecraftPlayerListProperty
import java.util.*
import java.util.concurrent.BlockingQueue
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MinecraftConnectedPlayer(
	val profile: MinecraftPlayerProfileResult,
	val packetMailbox: BlockingQueue<MinecraftPacket>,
	var lastPing: Duration = (-1).toDuration(DurationUnit.MILLISECONDS),
	val trackingPlayers: MutableSet<UUID> = mutableSetOf()
) {
	fun playerListEntry() = MinecraftPlayerListAddEntry(
		profile.name, MinecraftGameMode.CREATIVE,
		profile.properties.associate {
			it.name to MinecraftPlayerListProperty(it.value, it.signature)
		},
		lastPing
	)
}