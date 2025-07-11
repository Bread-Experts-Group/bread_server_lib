package org.bread_experts_group.coder.format.parse.gamemaker_win.chunk

import org.bread_experts_group.coder.format.parse.gamemaker_win.structure.GameMakerWINAudio

data class GameMakerWINAudiosChunk(
	override val offset: Long,
	val audios: List<GameMakerWINAudio>
) : GameMakerWINChunk("AUDO", offset) {
	override fun toString(): String = super.toString() + "[${audios.size} audio(s)]"
}