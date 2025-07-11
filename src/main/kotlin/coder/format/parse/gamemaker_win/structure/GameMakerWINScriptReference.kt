package org.bread_experts_group.coder.format.parse.gamemaker_win.structure

data class GameMakerWINScriptReference(
	val name: String,
	val id: Int
) {
	override fun toString(): String = "GameMakerWINScriptReference[$name, $id]"
}