package org.bread_experts_group.protocol.minecraft.packet.play

import org.bread_experts_group.coder.Mappable

enum class MinecraftChatMode(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftChatMode, Int> {
	ENABLED(0, "Enabled"),
	COMMANDS_ONLY(1, "Commands Only"),
	HIDDEN(2, "Hidden");

	override fun toString(): String = stringForm()
}