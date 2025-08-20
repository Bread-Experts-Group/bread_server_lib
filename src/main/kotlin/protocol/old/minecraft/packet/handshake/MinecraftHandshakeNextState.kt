package org.bread_experts_group.protocol.old.minecraft.packet.handshake

import org.bread_experts_group.coder.Mappable

enum class MinecraftHandshakeNextState(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftHandshakeNextState, Int> {
	STATUS(1, "Status"),
	LOGIN(2, "Login");

	override fun toString(): String = stringForm()
}