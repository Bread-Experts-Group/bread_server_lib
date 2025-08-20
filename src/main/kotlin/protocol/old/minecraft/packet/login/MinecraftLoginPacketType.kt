package org.bread_experts_group.protocol.old.minecraft.packet.login

import org.bread_experts_group.coder.Mappable
import org.bread_experts_group.protocol.old.minecraft.packet.MinecraftSide
import org.bread_experts_group.protocol.old.minecraft.packet.SidedIdentifier

enum class MinecraftLoginPacketType(
	override val id: SidedIdentifier,
	override val tag: String
) : Mappable<MinecraftLoginPacketType, SidedIdentifier> {
	DISCONNECT(SidedIdentifier(MinecraftSide.TO_CLIENT, 0x00), "Disconnect"),
	LOGIN_START(SidedIdentifier(MinecraftSide.TO_SERVER, 0x00), "Login Start"),
	LOGIN_SUCCESS(SidedIdentifier(MinecraftSide.TO_CLIENT, 0x02), "Login Success");

	override fun toString(): String = stringForm()
}