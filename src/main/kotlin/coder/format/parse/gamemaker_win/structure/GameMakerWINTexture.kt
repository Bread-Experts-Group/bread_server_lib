package org.bread_experts_group.coder.format.parse.gamemaker_win.structure

data class GameMakerWINTexture(
	val offset: Long,
	val scaled: Long,
	val generatedMips: Long,
	val textureBlockSize: Long,
	val textureWidth: Int,
	val textureHeight: Int,
	val indexInGroup: Int,
	val image: Long
) {
	override fun toString(): String = "GameMakerWINTexture@$offset[$textureWidth x $textureHeight, $indexInGroup | " +
			"$scaled | $generatedMips | $textureBlockSize, @$image"
}