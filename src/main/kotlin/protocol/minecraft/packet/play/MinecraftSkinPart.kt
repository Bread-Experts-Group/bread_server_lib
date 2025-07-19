package org.bread_experts_group.protocol.minecraft.packet.play

import org.bread_experts_group.coder.Flaggable

enum class MinecraftSkinPart(override val position: Long) : Flaggable {
	CAPE(0b00000001),
	JACKET(0b00000010),
	LEFT_SLEEVE(0b00000100),
	RIGHT_SLEEVE(0b00001000),
	LEFT_PANTS(0b00010000),
	RIGHT_PANTS(0b00100000),
	HAT(0b01000000);
}