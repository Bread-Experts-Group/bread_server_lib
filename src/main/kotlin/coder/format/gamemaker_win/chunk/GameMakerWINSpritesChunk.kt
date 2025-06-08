package org.bread_experts_group.coder.format.gamemaker_win.chunk

import org.bread_experts_group.coder.format.gamemaker_win.structure.GameMakerWINSprite

data class GameMakerWINSpritesChunk(
	override val offset: Long,
	val groups: List<GameMakerWINSprite>
) : GameMakerWINChunk("SPRT", offset) {
	override fun toString(): String = super.toString() + "[${groups.size} sprite(s)]"
}