package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.coder.Mappable

enum class MinecraftDifficulty(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftDifficulty, Int> {
	PEACEFUL(0, "Peaceful"),
	EASY(1, "Easy"),
	NORMAL(2, "Normal"),
	HARD(3, "Hard");

	override fun toString(): String = stringForm()
}