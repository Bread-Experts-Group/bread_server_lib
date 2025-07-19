package org.bread_experts_group.protocol.minecraft.packet.play

import org.bread_experts_group.coder.Mappable

enum class MinecraftGameMode(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftGameMode, Int> {
	SURVIVAL(0, "Survival Mode"),
	CREATIVE(1, "Creative Mode"),
	ADVENTURE(2, "Adventure Mode"),
	SPECTATOR(3, "Spectator Mode");

	override fun toString(): String = stringForm()
}