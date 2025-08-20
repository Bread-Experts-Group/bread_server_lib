package org.bread_experts_group.protocol.old.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayClickWindowPacket(
	val window: UByte,
	val slot: Short,
	val button: Byte,
	val actionNumber: Short,
	val mode: Int,
	val item: MinecraftSlot,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.CLICK_WINDOW, data) {
	override fun toString(): String = super.toString() + "[@$window, $mode:$slot-$button [$actionNumber], $item]"
}