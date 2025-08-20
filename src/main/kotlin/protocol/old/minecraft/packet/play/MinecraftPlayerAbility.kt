package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.coder.Flaggable

enum class MinecraftPlayerAbility(override val position: Long) : Flaggable {
	CREATIVE(0b00000001),
	FLYING(0b00000010),
	FLIGHT_ALLOWED(0b00000100),
	GOD_MODE(0b00001000)
}