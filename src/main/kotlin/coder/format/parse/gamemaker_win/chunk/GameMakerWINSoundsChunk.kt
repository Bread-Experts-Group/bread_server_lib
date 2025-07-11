package org.bread_experts_group.coder.format.parse.gamemaker_win.chunk

import org.bread_experts_group.coder.format.parse.gamemaker_win.structure.GameMakerWINSoundReference

data class GameMakerWINSoundsChunk(
	override val offset: Long,
	val sounds: List<GameMakerWINSoundReference>
) : GameMakerWINChunk("SOND", offset) {
	override fun toString(): String = super.toString() + "[${sounds.size} sound(s)]"
}