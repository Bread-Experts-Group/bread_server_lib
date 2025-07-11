package org.bread_experts_group.coder.format.parse.gamemaker_win.structure

data class GameMakerWINSprite(
	val offset: Long,
	val name: String,
	val width: Int,
	val height: Int,
	val marginLeft: Int,
	val marginRight: Int,
	val marginBottom: Int,
	val marginTop: Int,
	val transparent: Int,
	val smooth: Int,
	val preload: Int,
	val bboxMode: Int,
	val sepMasks: Int,
	val originX: Int,
	val originY: Int
) {
	override fun toString(): String = "GameMakerWINSprite@$offset[$name, $width x $height, " + "" +
			"[$marginLeft, $marginRight, $marginBottom, $marginTop], " + "[${
		buildList {
			if (transparent != 0) add("TRANSPARENT")
			if (smooth != 0) add("SMOOTH")
			if (preload != 0) add("PRELOAD")
		}.joinToString(",")
	}], $bboxMode | $sepMasks, origin: $originX x $originY"
}