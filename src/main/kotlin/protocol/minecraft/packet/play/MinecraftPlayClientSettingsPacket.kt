package org.bread_experts_group.protocol.minecraft.packet.play

import java.nio.channels.ReadableByteChannel
import java.util.*

class MinecraftPlayClientSettingsPacket(
	val locale: Locale,
	val viewDistance: Byte,
	val chatMode: MinecraftChatMode,
	val chatColors: Boolean,
	val displayedSkinParts: EnumSet<MinecraftSkinPart>,
	val mainHand: MinecraftMainHand,
	data: ReadableByteChannel
) : MinecraftPlayPacket(MinecraftPlayPacketType.CLIENT_SETTINGS, data) {
	override fun toString(): String = super.toString() + "[$locale, $viewDistance chunks view, chat: $chatMode" +
			(if (chatColors) " (with colors)" else "") + ", displayed skin: $displayedSkinParts, $mainHand]"
}