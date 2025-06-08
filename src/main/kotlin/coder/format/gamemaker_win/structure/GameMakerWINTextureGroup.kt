package org.bread_experts_group.coder.format.gamemaker_win.structure

data class GameMakerWINTextureGroup(
	val offset: Long,
	val name: String,
	val directory: String,
	val extension: String,
	val loadType: Int,
	val textureOffset: Long,
	val spriteOffset: Long,
	val spineOffset: Long,
	val fontOffset: Long,
	val tilesetOffset: Long,
) {
	override fun toString(): String = "GameMakerWINAudio@$offset[$name@$directory, $extension, $loadType, " +
			"textures@$textureOffset, sprites@$spriteOffset, spineSprites@$spineOffset, fonts@$fontOffset, " +
			"tilesets@$tilesetOffset]"
}