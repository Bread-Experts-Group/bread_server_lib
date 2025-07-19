package org.bread_experts_group.protocol.minecraft.packet.play

import org.bread_experts_group.coder.Mappable

enum class MinecraftHand(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftHand, Int> {
	MAIN_HAND(0, "Main Hand"),
	OFF_HAND(1, "Off-Hand");

	override fun toString(): String = stringForm()
}