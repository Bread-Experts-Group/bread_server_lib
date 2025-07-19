package org.bread_experts_group.protocol.minecraft.packet.play

import org.bread_experts_group.coder.Mappable

enum class MinecraftCraftingBookDataType(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftCraftingBookDataType, Int> {
	DISPLAYED_RECIPE(0, "Displayed Recipe"),
	STATUS(1, "Status");

	override fun toString(): String = stringForm()
}