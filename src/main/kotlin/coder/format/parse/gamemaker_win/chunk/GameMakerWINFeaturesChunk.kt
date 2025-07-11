package org.bread_experts_group.coder.format.parse.gamemaker_win.chunk

data class GameMakerWINFeaturesChunk(
	override val offset: Long,
	val features: List<String>
) : GameMakerWINChunk("FEAT", offset) {
	override fun toString(): String = super.toString() + "[${features.size} features(s)][${features.joinToString(",")}]"
}