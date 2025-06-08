package org.bread_experts_group.coder.format.gamemaker_win.structure

data class GameMakerWINAudioGroup(
	val offset: Long,
	val name: String
) {
	override fun toString(): String = "GameMakerWINAudio@$offset[$name]"
}