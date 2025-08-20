package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.numeric.geometry.Point3D
import java.nio.channels.ReadableByteChannel

class MinecraftPlayPlayerPositionPacket(
	val position: Point3D,
	val grounded: Boolean,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.PLAYER_POSITION, data) {
	override fun toString(): String = super.toString() + "[$position${if (grounded) " / grounded" else ""}]"
}