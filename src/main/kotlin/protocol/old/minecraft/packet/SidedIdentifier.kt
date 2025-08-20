package org.bread_experts_group.protocol.old.minecraft.packet

import org.bread_experts_group.hex

data class SidedIdentifier(
	val side: MinecraftSide,
	val id: Int
) {
	override fun toString(): String = "$side: ${hex(id.toUInt())}"
}