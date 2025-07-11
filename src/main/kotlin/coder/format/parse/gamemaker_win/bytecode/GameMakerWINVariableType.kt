package org.bread_experts_group.coder.format.parse.gamemaker_win.bytecode

enum class GameMakerWINVariableType(val code: Int) {
	FUNCTION(0x80),
	VARIABLE(0xA0);

	companion object {
		val mapping: Map<Int, GameMakerWINVariableType> = entries.associateBy(GameMakerWINVariableType::code)
	}
}