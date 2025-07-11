package org.bread_experts_group.coder.format.parse.gamemaker_win.chunk

data class GameMakerWINStringsChunk(
	override val offset: Long,
	val strings: List<String>
) : GameMakerWINChunk("STRG", offset) {
	override fun toString(): String = super.toString() + "[${strings.size} string(s)]"
}