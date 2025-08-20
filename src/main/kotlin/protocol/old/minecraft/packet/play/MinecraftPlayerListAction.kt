package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.coder.Mappable

enum class MinecraftPlayerListAction(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftPlayerListAction, Int> {
	ADD(0, "Add Player"),
	UPDATE_GAMEMODE(1, "Update Player Gamemode"),
	UPDATE_PING(2, "Update Player Latency"),
	UPDATE_NAME(3, "Update Player Display Name"),
	REMOVE(4, "Remove Player");

	override fun toString(): String = stringForm()
}