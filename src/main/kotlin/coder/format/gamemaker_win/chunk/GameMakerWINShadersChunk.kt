package org.bread_experts_group.coder.format.gamemaker_win.chunk

import org.bread_experts_group.coder.format.gamemaker_win.structure.GameMakerWINShader

data class GameMakerWINShadersChunk(
	override val offset: Long,
	val shaders: List<GameMakerWINShader>
) : GameMakerWINChunk("SHDR", offset) {
	override fun toString(): String = super.toString() + "[${shaders.size} shader(s)]"
}