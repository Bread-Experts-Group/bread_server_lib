package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.coder.Mappable

enum class MinecraftFace(
	override val id: Byte,
	override val tag: String
) : Mappable<MinecraftFace, Byte> {
	BOTTOM(0, "Bottom (-Y)"),
	TOP(1, "Top (+Y)"),
	NORTH(2, "North (-Z)"),
	SOUTH(3, "South (+Z)"),
	WEST(4, "West (-X)"),
	EAST(5, "East (+X)");

	override fun toString(): String = stringForm()
}