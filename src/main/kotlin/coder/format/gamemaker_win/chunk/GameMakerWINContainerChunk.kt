package org.bread_experts_group.coder.format.gamemaker_win.chunk

class GameMakerWINContainerChunk(
	tag: String,
	override val offset: Long,
	val chunks: List<GameMakerWINChunk>
) : GameMakerWINChunk(tag, offset) {
	override fun toString(): String = super.toString() + "[${chunks.size}]\n\t[${
		chunks.joinToString(",\n\t") { it.toString().replace("\n", "\n\t") }
	}]"
}