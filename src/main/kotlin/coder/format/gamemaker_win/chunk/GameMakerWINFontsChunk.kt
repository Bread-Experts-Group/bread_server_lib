package org.bread_experts_group.coder.format.gamemaker_win.chunk

import org.bread_experts_group.coder.format.gamemaker_win.structure.GameMakerWINFont

data class GameMakerWINFontsChunk(
	override val offset: Long,
	val fonts: List<GameMakerWINFont>
) : GameMakerWINChunk("FONT", offset) {
	override fun toString(): String = super.toString() + "[${fonts.size} font(s)]"
}