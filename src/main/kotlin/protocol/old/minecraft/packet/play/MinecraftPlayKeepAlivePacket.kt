package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.channel.ByteArrayChannel
import org.bread_experts_group.channel.bytes
import org.bread_experts_group.protocol.old.minecraft.varSizeOfNi
import java.nio.channels.SeekableByteChannel

class MinecraftPlayKeepAlivePacket(
	val long: Long
) : MinecraftPlayPacket(
	MinecraftPlayPacketType.KEEP_ALIVE_TC,
	ByteArrayChannel(long.bytes())
) {
	override fun computeSize(): Int = varSizeOfNi(type.id.id) + (data as SeekableByteChannel).size().toInt()
}