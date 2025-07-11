package org.bread_experts_group.coder.format.parse.gamemaker_win.chunk

import org.bread_experts_group.coder.format.parse.gamemaker_win.structure.GameMakerWINBytecode

data class GameMakerWINBytecodeChunk(
	override val offset: Long,
	val bytecode: List<GameMakerWINBytecode>
) : GameMakerWINChunk("CODE", offset) {
	override fun toString(): String = super.toString() + "[${bytecode.size} bytecode segment(s)]"
}