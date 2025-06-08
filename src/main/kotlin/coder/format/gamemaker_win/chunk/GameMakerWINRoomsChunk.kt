package org.bread_experts_group.coder.format.gamemaker_win.chunk

import org.bread_experts_group.coder.format.gamemaker_win.structure.GameMakerWINRoom

data class GameMakerWINRoomsChunk(
	override val offset: Long,
	val rooms: List<GameMakerWINRoom>
) : GameMakerWINChunk("ROOM", offset) {
	override fun toString(): String = super.toString() + "[${rooms.size} room(s)]"
}