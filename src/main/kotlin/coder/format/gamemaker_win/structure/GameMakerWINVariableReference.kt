package org.bread_experts_group.coder.format.gamemaker_win.structure

data class GameMakerWINVariableReference(
	val offset: Long,
	val name: String,
	val instanceType: Int,
	val variableID: Int,
	val runCount: Long,
	val firstOccurrence: Long?
) {
	override fun toString(): String = "GameMakerWINVariableReference@$offset[$name, $runCount exec.," +
			" first at ${firstOccurrence ?: "nowhere"}]"
}