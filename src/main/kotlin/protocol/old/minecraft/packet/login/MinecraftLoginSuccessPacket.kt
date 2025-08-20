package org.bread_experts_group.protocol.old.minecraft.packet.login

import org.bread_experts_group.channel.ByteArrayChannel
import org.bread_experts_group.protocol.old.minecraft.string
import org.bread_experts_group.protocol.old.minecraft.varSizeOfNi
import java.nio.channels.SeekableByteChannel
import java.util.*

class MinecraftLoginSuccessPacket(
	val uuid: UUID,
	val username: String
) : MinecraftLoginPacket(
	MinecraftLoginPacketType.LOGIN_SUCCESS,
	run {
		ByteArrayChannel(string(uuid.toString()) + string(username))
	}
) {
	override fun computeSize(): Int = varSizeOfNi(type.id.id) + (data as SeekableByteChannel).size().toInt()
}