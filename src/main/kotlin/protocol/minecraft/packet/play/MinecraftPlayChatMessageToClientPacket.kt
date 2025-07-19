package org.bread_experts_group.protocol.minecraft.packet.play

import org.bread_experts_group.channel.ByteArrayChannel
import org.bread_experts_group.coder.fixed.json.JSONObject
import org.bread_experts_group.coder.fixed.json.JSONString
import org.bread_experts_group.protocol.minecraft.string
import org.bread_experts_group.protocol.minecraft.varSizeOfNi
import java.nio.channels.SeekableByteChannel

class MinecraftPlayChatMessageToClientPacket(
	val message: String,
	val position: MinecraftChatMessagePosition
) : MinecraftPlayPacket(
	MinecraftPlayPacketType.CHAT_MESSAGE_TC,
	run {
		val chat = string(
			JSONObject(
				"text" to JSONString(message)
			).toString()
		)
		ByteArrayChannel(chat + position.id.toByte())
	}
) {
	override fun computeSize(): Int = varSizeOfNi(type.id.id) + (data as SeekableByteChannel).size().toInt()
}