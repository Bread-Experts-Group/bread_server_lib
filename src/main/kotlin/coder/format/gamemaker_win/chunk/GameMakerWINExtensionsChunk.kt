package org.bread_experts_group.coder.format.gamemaker_win.chunk

import org.bread_experts_group.coder.format.gamemaker_win.structure.GameMakerWINExtension

data class GameMakerWINExtensionsChunk(
	override val offset: Long,
	val extensions: List<GameMakerWINExtension>
) : GameMakerWINChunk("EXTN", offset) {
	override fun toString(): String = super.toString() + "[${extensions.size} extension(s)]"
}