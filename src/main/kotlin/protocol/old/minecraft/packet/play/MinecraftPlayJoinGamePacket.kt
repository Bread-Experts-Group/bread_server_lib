package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.channel.ByteBufferChannel
import org.bread_experts_group.protocol.old.minecraft.string
import org.bread_experts_group.protocol.old.minecraft.varSizeOfNi
import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel

class MinecraftPlayJoinGamePacket(
	val entityID: Int,
	val gameMode: MinecraftGameMode,
	val hardcode: Boolean,
	val dimension: Int,
	val difficulty: MinecraftDifficulty,
	val levelType: String,
	val reducedDebugInfo: Boolean = false,
	val maxPlayers: Int = 0,
) : MinecraftPlayPacket(
	MinecraftPlayPacketType.JOIN_GAME,
	run {
		val buffer = ByteBuffer.allocate(28)
		buffer.putInt(entityID)
		buffer.put((gameMode.id or (if (hardcode) 0x8 else 0)).toByte())
		buffer.putInt(dimension)
		buffer.put(difficulty.id.toByte())
		buffer.put(maxPlayers.toByte())
		buffer.put(string(levelType))
		buffer.put(if (reducedDebugInfo) 1 else 0)
		buffer.flip()
		ByteBufferChannel(buffer)
	}
) {
	override fun computeSize(): Int = varSizeOfNi(type.id.id) + (data as SeekableByteChannel).size().toInt()
}