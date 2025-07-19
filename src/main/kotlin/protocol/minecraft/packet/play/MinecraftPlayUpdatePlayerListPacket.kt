package org.bread_experts_group.protocol.minecraft.packet.play

import org.bread_experts_group.channel.ByteBufferChannel
import org.bread_experts_group.protocol.minecraft.varNi
import org.bread_experts_group.protocol.minecraft.varSizeOfNi
import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel
import java.util.*

sealed class MinecraftPlayUpdatePlayerListPacket(
	val action: MinecraftPlayerListAction,
	players: Map<UUID, SeekableByteChannel>
) : MinecraftPlayPacket(
	MinecraftPlayPacketType.UPDATE_PLAYER_LIST,
	run {
		val buffer = ByteBuffer.allocate(16 + (players.size * 16) + players.values.sumOf { it.size().toInt() })
		buffer.put(varNi(action.id))
		buffer.put(varNi(players.size))
		players.forEach {
			buffer.putLong(it.key.mostSignificantBits)
			buffer.putLong(it.key.leastSignificantBits)
			it.value.read(buffer)
		}
		ByteBufferChannel(buffer.flip())
	}
) {
	override fun toString(): String = super.toString() + "[$action]"
	override fun computeSize(): Int = varSizeOfNi(type.id.id) + (data as SeekableByteChannel).size().toInt()
}