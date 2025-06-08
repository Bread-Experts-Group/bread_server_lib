package org.bread_experts_group.coder.format.gamemaker_win.chunk

data class GameMakerWINTagsChunk(
	override val offset: Long,
	val tags: List<String>,
	val tagged: Map<Int, List<String>>
) : GameMakerWINChunk("TAGS", offset) {
	override fun toString(): String = super.toString() + "[${tags.size} tag(s), ${tagged.size} tagged object(s)]"
}