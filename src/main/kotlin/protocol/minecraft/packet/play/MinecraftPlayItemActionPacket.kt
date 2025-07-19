package org.bread_experts_group.protocol.minecraft.packet.play

import org.bread_experts_group.numeric.geometry.Point3I
import java.nio.channels.ReadableByteChannel

class MinecraftPlayItemActionPacket(
	val action: MinecraftItemAction,
	val position: Point3I,
	val face: MinecraftFace,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.ITEM_ACTION, data) {
	override fun toString(): String = super.toString() + "[$action @ $position, $face]"
}