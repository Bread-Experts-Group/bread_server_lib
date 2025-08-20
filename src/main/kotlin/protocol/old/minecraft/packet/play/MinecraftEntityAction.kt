package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.coder.Mappable

enum class MinecraftEntityAction(
	override val id: Int,
	override val tag: String
) : Mappable<MinecraftEntityAction, Int> {
	START_SNEAK(0, "Start Sneaking"),
	STOP_SNEAK(1, "Stop Sneaking"),
	LEAVE_BED(2, "Leave Bed"),
	START_SPRINT(3, "Start Sprint"),
	STOP_SPRINT(4, "Stop Sprint"),
	START_HORSE_JUMP(5, "Start Horse Jump"),
	STOP_HORSE_JUMP(6, "Stop Horse Jump"),
	OPEN_HORSE_INVENTORY(7, "Open Horse Inventory"),
	START_ELYTRA_FLIGHT(8, "Start Elytra Flight");

	override fun toString(): String = stringForm()
}