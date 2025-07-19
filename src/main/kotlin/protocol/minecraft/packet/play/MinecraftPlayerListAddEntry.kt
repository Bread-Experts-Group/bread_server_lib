package org.bread_experts_group.protocol.minecraft.packet.play

import kotlin.time.Duration

data class MinecraftPlayerListAddEntry(
	val name: String,
	val gameMode: MinecraftGameMode,
	val properties: Map<String, MinecraftPlayerListProperty>,
	val ping: Duration,
	val displayName: String? = null
)