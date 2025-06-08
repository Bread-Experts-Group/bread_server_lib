package org.bread_experts_group.coder.format.gamemaker_win.structure

data class GameMakerWINImageReference(
	val name: String,
	val offset: Long
) {
	override fun toString(): String = "GameMakerWINImageReference@$offset[$name]"
}