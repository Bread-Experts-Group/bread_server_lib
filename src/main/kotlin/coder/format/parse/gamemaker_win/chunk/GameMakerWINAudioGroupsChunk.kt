package org.bread_experts_group.coder.format.parse.gamemaker_win.chunk

import org.bread_experts_group.coder.format.parse.gamemaker_win.structure.GameMakerWINAudioGroup

data class GameMakerWINAudioGroupsChunk(
	override val offset: Long,
	val groups: List<GameMakerWINAudioGroup>
) : GameMakerWINChunk("AGRP", offset) {
	override fun toString(): String = super.toString() + "[${groups.size} audio group(s)]"
}