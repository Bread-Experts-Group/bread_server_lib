package org.bread_experts_group.coder.format.parse.gamemaker_win.chunk

import org.bread_experts_group.coder.format.parse.gamemaker_win.structure.GameMakerWINImageReference

data class GameMakerWINImagesChunk(
	override val offset: Long,
	val images: List<GameMakerWINImageReference>
) : GameMakerWINChunk("EMBI", offset) {
	override fun toString(): String = super.toString() + "[${images.size} image(s)]"
}