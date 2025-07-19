package org.bread_experts_group.protocol.minecraft.packet.play

import org.bread_experts_group.coder.Mappable

enum class MinecraftAdvancementTabAction(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftAdvancementTabAction, Int> {
	OPENED(0, "Opened Tab"),
	CLOSED(1, "Closed Tab");

	override fun toString(): String = stringForm()
}