package org.bread_experts_group.coder.format.gamemaker_win.chunk

import org.bread_experts_group.coder.format.gamemaker_win.structure.GameMakerWINFunctionReference

data class GameMakerWINFunctionsChunk(
	override val offset: Long,
	val functions: List<GameMakerWINFunctionReference>
) : GameMakerWINChunk("FUNC", offset) {
	override fun toString(): String = super.toString() + "[${functions.size} function(s)]"
}