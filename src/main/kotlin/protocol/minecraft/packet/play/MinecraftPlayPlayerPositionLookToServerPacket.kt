package org.bread_experts_group.protocol.minecraft.packet.play

import org.bread_experts_group.numeric.geometry.Point3D
import java.nio.channels.ReadableByteChannel

class MinecraftPlayPlayerPositionLookToServerPacket(
	val position: Point3D,
	val yaw: Float,
	val pitch: Float,
	val grounded: Boolean,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.PLAYER_POSITION_LOOK_TS, data) {
	override fun toString(): String = super.toString() + "[$position / $yaw* $pitch*" +
			"${if (grounded) " / grounded" else ""}]"
}