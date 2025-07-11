package org.bread_experts_group.coder.format.parse.gamemaker_win.structure

data class GameMakerWINExtension(
	val offset: Long,
	val folderName: String,
	val name: String,
	val version: String,
	val unkA: String,
	val unkB: Int,
	val unkC: Int
) {
	override fun toString(): String = "GameMakerWINExtension@$offset[$name@$folderName, $version, " +
			"$unkA | $unkB | $unkC]"
}