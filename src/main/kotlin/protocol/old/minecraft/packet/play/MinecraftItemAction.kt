package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.coder.Mappable

enum class MinecraftItemAction(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftItemAction, Int> {
	DIG_START(0, "Start Digging"),
	DIG_CANCEL(1, "Cancel Digging"),
	DIG_FINISH(2, "Finish Digging"),
	DROP_ITEM_STACK(3, "Drop Item Stack"),
	DROP_ITEM(4, "Drop Item"),
	USE_ITEM(5, "Use Item"),
	SWAP_ITEM(6, "Swap Item");

	override fun toString(): String = stringForm()
}