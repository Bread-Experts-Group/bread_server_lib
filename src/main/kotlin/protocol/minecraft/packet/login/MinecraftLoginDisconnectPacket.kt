package org.bread_experts_group.protocol.minecraft.packet.login

import org.bread_experts_group.channel.ByteArrayChannel
import org.bread_experts_group.coder.fixed.json.JSONObject
import org.bread_experts_group.coder.fixed.json.JSONString
import org.bread_experts_group.protocol.minecraft.string
import org.bread_experts_group.protocol.minecraft.varSizeOfNi
import java.nio.channels.SeekableByteChannel

class MinecraftLoginDisconnectPacket(
	val reason: String
) : MinecraftLoginPacket(
	MinecraftLoginPacketType.DISCONNECT,
	run {
		val chat = JSONObject(
			"text" to JSONString(reason)
		)
		ByteArrayChannel(string(chat.toString()))
	}
) {
	override fun computeSize(): Int = varSizeOfNi(type.id.id) + (data as SeekableByteChannel).size().toInt()
}