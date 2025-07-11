package org.bread_experts_group.coder.format.parse.gamemaker_win.chunk

import org.bread_experts_group.coder.format.parse.gamemaker_win.structure.GameMakerWINScriptReference

data class GameMakerWINScriptsChunk(
	override val offset: Long,
	val scripts: List<GameMakerWINScriptReference>
) : GameMakerWINChunk("SCPT", offset) {
	override fun toString(): String = super.toString() + "[${scripts.size} script(s)]"
}