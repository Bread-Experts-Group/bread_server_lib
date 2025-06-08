package org.bread_experts_group.coder.format.gamemaker_win.structure

data class GameMakerWINFont(
	val gmlName: String,
	val systemName: String,
	val emSize: Int,
	val bold: Int,
	val italic: Int,
	val unicodeRangeStart: Int,
	val charset: Int,
	val antialiasing: Int,
	val unicodeRangeEnd: Int,
	val textureOffset: Long,
	val scaleX: Float,
	val scaleY: Float,
	val ascenderOffset: Int,
	val ascender: Int,
	val sdfSpread: Int,
	val lineHeight: Int,
	val glyphs: List<GameMakerWINFontGlyph>
) {
	override fun toString(): String = "GameMakerWINFont[$gmlName, $systemName, $scaleX x $scaleY, " +
			"$unicodeRangeStart-$unicodeRangeStart, $charset, texture@$textureOffset, " +
			"[${
				buildList {
					if (italic != 0) add("ITALIC")
					if (bold != 0) add("BOLD")
					if (antialiasing != 0) add("ANTI-ALIASING")
				}.joinToString(",")
			}], ${glyphs.size} glyphs]"
}