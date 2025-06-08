package org.bread_experts_group.coder.format.gamemaker_win.chunk

import org.bread_experts_group.coder.format.gamemaker_win.structure.GameMakerWINObject

data class GameMakerWINObjectsChunk(
	override val offset: Long,
	val objects: List<GameMakerWINObject>
) : GameMakerWINChunk("OBJT", offset) {
	override fun toString(): String = super.toString() + "[${objects.size} object(s)]"
}