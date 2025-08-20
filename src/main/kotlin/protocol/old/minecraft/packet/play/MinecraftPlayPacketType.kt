package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.coder.Mappable
import org.bread_experts_group.protocol.old.minecraft.packet.MinecraftSide
import org.bread_experts_group.protocol.old.minecraft.packet.SidedIdentifier

enum class MinecraftPlayPacketType(
	override val id: SidedIdentifier,
	override val tag: String
) : Mappable<MinecraftPlayPacketType, SidedIdentifier> {
	TELEPORT_CONFIRM(SidedIdentifier(MinecraftSide.TO_SERVER, 0x00), "Teleport Confirm"),
	CHAT_MESSAGE_TS(SidedIdentifier(MinecraftSide.TO_SERVER, 0x02), "Chat Message"),
	CLIENT_STATUS(SidedIdentifier(MinecraftSide.TO_SERVER, 0x03), "Client Status"),
	CLIENT_SETTINGS(SidedIdentifier(MinecraftSide.TO_SERVER, 0x04), "Client Settings"),
	CLICK_WINDOW(SidedIdentifier(MinecraftSide.TO_SERVER, 0x07), "Click Window"),
	STATISTICS(SidedIdentifier(MinecraftSide.TO_CLIENT, 0x07), "Statistics"),
	CLOSE_WINDOW_TS(SidedIdentifier(MinecraftSide.TO_SERVER, 0x08), "Close Window"),
	PLUGIN_MESSAGE_TS(SidedIdentifier(MinecraftSide.TO_SERVER, 0x09), "Plugin Message"),
	KEEP_ALIVE_TS(SidedIdentifier(MinecraftSide.TO_SERVER, 0x0B), "Keep-Alive"),
	PLAYER_POSITION(SidedIdentifier(MinecraftSide.TO_SERVER, 0x0D), "Player Position"),
	PLAYER_POSITION_LOOK_TS(SidedIdentifier(MinecraftSide.TO_SERVER, 0x0E), "Player Position & Look"),
	PLAYER_LOOK(SidedIdentifier(MinecraftSide.TO_SERVER, 0x0F), "Player Look"),
	CHAT_MESSAGE_TC(SidedIdentifier(MinecraftSide.TO_CLIENT, 0x0F), "Chat Message"),
	PLAYER_ABILITIES_TS(SidedIdentifier(MinecraftSide.TO_SERVER, 0x13), "Player Abilities"),
	ITEM_ACTION(SidedIdentifier(MinecraftSide.TO_SERVER, 0x14), "Item Action"),
	ENTITY_ACTION(SidedIdentifier(MinecraftSide.TO_SERVER, 0x15), "Entity Action"),
	CRAFTING_BOOK_DATA(SidedIdentifier(MinecraftSide.TO_SERVER, 0x17), "Crafting Book Data"),
	ADVANCEMENT_TAB(SidedIdentifier(MinecraftSide.TO_SERVER, 0x19), "Advancement Tab"),
	SLOT_CHANGED(SidedIdentifier(MinecraftSide.TO_SERVER, 0x1A), "Slot Changed"),
	DISCONNECT(SidedIdentifier(MinecraftSide.TO_CLIENT, 0x1A), "Disconnect"),
	CREATIVE_INVENTORY(SidedIdentifier(MinecraftSide.TO_SERVER, 0x1B), "Creative Inventory"),
	ANIMATION_TS(SidedIdentifier(MinecraftSide.TO_SERVER, 0x1D), "Animation"),
	KEEP_ALIVE_TC(SidedIdentifier(MinecraftSide.TO_CLIENT, 0x1F), "Keep-Alive"),
	USE_ITEM(SidedIdentifier(MinecraftSide.TO_SERVER, 0x20), "Use Item"),
	CHUNK_DATA(SidedIdentifier(MinecraftSide.TO_CLIENT, 0x20), "Chunk Data"),
	JOIN_GAME(SidedIdentifier(MinecraftSide.TO_CLIENT, 0x23), "Join Game"),
	UPDATE_PLAYER_LIST(SidedIdentifier(MinecraftSide.TO_CLIENT, 0x2E), "Update Player List"),
	PLAYER_POSITION_LOOK_TC(SidedIdentifier(MinecraftSide.TO_CLIENT, 0x2F), "Player Position & Look");

	override fun toString(): String = stringForm()
}