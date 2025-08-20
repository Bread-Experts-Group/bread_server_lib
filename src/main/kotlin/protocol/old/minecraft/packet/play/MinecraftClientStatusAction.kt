package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.coder.Mappable

enum class MinecraftClientStatusAction(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftClientStatusAction, Int> {
	RESPAWN(0, "Respawn"),
	DOWNLOAD_STATISTICS(1, "Download Statistics");

	override fun toString(): String = stringForm()
}