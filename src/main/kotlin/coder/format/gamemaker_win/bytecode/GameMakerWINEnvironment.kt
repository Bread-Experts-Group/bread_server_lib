package org.bread_experts_group.coder.format.gamemaker_win.bytecode

enum class GameMakerWINEnvironment(val code: Int) {
	LOCAL_FUNCTION(0x0000),
	ARGUMENT(0xFFF1),
	LOCAL(0xFFF9),
	INSTANCE_LOCAL(0xFFFA),
	GLOBAL(0xFFFB);

	companion object {
		val mapping: Map<Int, GameMakerWINEnvironment> = entries.associateBy(GameMakerWINEnvironment::code)
	}
}