package org.bread_experts_group.coder.format.gamemaker_win.structure

data class GameMakerWINFunctionReference(
	val offset: Long,
	val name: String,
	val runCount: Long,
	val firstOccurrence: Long
) {
	override fun toString(): String = "GameMakerWINFunctionReference@$offset[$name, $runCount exec., " +
			"first @ $firstOccurrence]"
}