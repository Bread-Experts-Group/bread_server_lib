package org.bread_experts_group.coder.format.gamemaker_win.structure

data class GameMakerWINSoundReference(
	val name: String,
	val flags: Int,
	val type: String?,
	val file: String,
	val effects: Int,
	val volume: Float,
	val pitch: Float,
	val length: Float
) {
	override fun toString(): String = "GameMakerWINSoundReference" +
			"[$name@$file, $type, $flags | $effects, $volume, $pitch, $length]"
}