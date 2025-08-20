package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.coder.Mappable

enum class MinecraftChatMessagePosition(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftChatMessagePosition, Int> {
	CHAT(0, "Chat"),
	SYSTEM_MESSAGE(1, "System Message"),
	GAME_INFO(2, "Game Info");

	override fun toString(): String = stringForm()
}