package org.bread_experts_group.coder.format.gamemaker_win.chunk

import org.bread_experts_group.coder.format.gamemaker_win.structure.GameMakerWINVariableReference

data class GameMakerWINVariablesChunk(
	override val offset: Long,
	val maxLocals: Int,
	val variables: List<GameMakerWINVariableReference>
) : GameMakerWINChunk("VARI", offset) {
	override fun toString(): String = super.toString() + "[${variables.size} variable(s)]"
}