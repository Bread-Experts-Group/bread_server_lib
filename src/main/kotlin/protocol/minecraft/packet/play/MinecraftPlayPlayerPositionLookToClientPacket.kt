package org.bread_experts_group.protocol.minecraft.packet.play

import org.bread_experts_group.channel.ByteBufferChannel
import org.bread_experts_group.numeric.geometry.Point3D
import org.bread_experts_group.numeric.geometry.put3D
import org.bread_experts_group.protocol.minecraft.varNi
import org.bread_experts_group.protocol.minecraft.varSizeOfNi
import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel

class MinecraftPlayPlayerPositionLookToClientPacket(
	val position: Point3D,
	val yaw: Float,
	val pitch: Float,
	val teleportID: Int
) : MinecraftPlayPacket(
	MinecraftPlayPacketType.PLAYER_POSITION_LOOK_TC,
	run {
		val buffer = ByteBuffer.allocate(41)
		buffer.put3D(position)
		buffer.putFloat(yaw)
		buffer.putFloat(pitch)
		buffer.put(0)
		buffer.put(varNi(teleportID))
		ByteBufferChannel(buffer.flip())
	}
) {
	override fun computeSize(): Int = varSizeOfNi(type.id.id) + (data as SeekableByteChannel).size().toInt()
}