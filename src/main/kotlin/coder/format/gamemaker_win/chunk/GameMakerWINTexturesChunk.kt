package org.bread_experts_group.coder.format.gamemaker_win.chunk

import org.bread_experts_group.coder.format.gamemaker_win.structure.GameMakerWINTexture

data class GameMakerWINTexturesChunk(
	override val offset: Long,
	val textures: List<GameMakerWINTexture>
) : GameMakerWINChunk("TXTR", offset) {
	override fun toString(): String = super.toString() + "[${textures.size} texture(s)]"
}