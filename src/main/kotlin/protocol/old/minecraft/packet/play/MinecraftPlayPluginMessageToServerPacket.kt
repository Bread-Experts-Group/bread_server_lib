package org.bread_experts_group.protocol.old.minecraft.packet.play

import java.nio.channels.ReadableByteChannel

class MinecraftPlayPluginMessageToServerPacket(
	val channel: String,
	pluginData: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.PLUGIN_MESSAGE_TS, pluginData) {
	override fun toString(): String = super.toString() + "[\"$channel\"]"
}