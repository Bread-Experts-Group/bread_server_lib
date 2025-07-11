package org.bread_experts_group.coder.format.parse.gamemaker_win.bytecode

enum class GameMakerWINDatatype(val code: Int) {
	DOUBLE(0),
	FLOAT(1),
	INTEGER(2),
	LONG(3),
	BOOLEAN(4),
	VARIABLE(5),
	STRING(6),
	DELETE(8),
	UNDEFINED(9),
	UNSIGNED_INTEGER(10),
	SHORT(15);

	companion object {
		val mapping: Map<Int, GameMakerWINDatatype> = entries.associateBy(GameMakerWINDatatype::code)
	}
}