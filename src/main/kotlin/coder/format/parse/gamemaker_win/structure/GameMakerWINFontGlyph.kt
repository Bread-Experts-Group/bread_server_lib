package org.bread_experts_group.coder.format.parse.gamemaker_win.structure

data class GameMakerWINFontGlyph(
	val codepoint: UShort,
	val tU: Short,
	val tV: Short,
	val tW: Short,
	val tH: Short,
	val shift: Short,
	val offsetUnk: Short
) {
	override fun toString(): String =
		"GameMakerWINFontGlyph[@$codepoint '${Char(codepoint)}', @[$tU x $tH], $tW x $tH [$shift | $offsetUnk]]"
}