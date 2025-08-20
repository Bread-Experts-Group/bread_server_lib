package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.coder.Mappable

enum class MinecraftMainHand(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftMainHand, Int> {
	LEFT(0, "Left Hand"),
	RIGHT(1, "Right Hand");

	override fun toString(): String = stringForm()
}