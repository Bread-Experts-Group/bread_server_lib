package org.bread_experts_group.coder.format.gamemaker_win.bytecode

enum class GameMakerWINOpcode(val code: Int, val variant: GameMakerWINOpcodeVariant) {
	CAST(0x07, GameMakerWINOpcodeVariant.DOUBLE_TYPE),
	MULTIPLY(0x08, GameMakerWINOpcodeVariant.DOUBLE_TYPE),
	DIVIDE(0x09, GameMakerWINOpcodeVariant.DOUBLE_TYPE),
	REMAINDER(0x0A, GameMakerWINOpcodeVariant.DOUBLE_TYPE),
	MODULUS(0x0B, GameMakerWINOpcodeVariant.DOUBLE_TYPE),
	ADD(0x0C, GameMakerWINOpcodeVariant.DOUBLE_TYPE),
	SUBTRACT(0x0D, GameMakerWINOpcodeVariant.DOUBLE_TYPE),
	AND(0x0E, GameMakerWINOpcodeVariant.DOUBLE_TYPE),
	OR(0x0F, GameMakerWINOpcodeVariant.DOUBLE_TYPE),
	XOR(0x10, GameMakerWINOpcodeVariant.DOUBLE_TYPE),
	NEGATE(0x11, GameMakerWINOpcodeVariant.SINGLE_TYPE),
	NOT(0x12, GameMakerWINOpcodeVariant.SINGLE_TYPE),
	SHIFT_LEFT(0x13, GameMakerWINOpcodeVariant.DOUBLE_TYPE),
	SHIFT_RIGHT(0x14, GameMakerWINOpcodeVariant.DOUBLE_TYPE),
	COMPARE(0x15, GameMakerWINOpcodeVariant.COMPARE),
	POP_INTO(0x45, GameMakerWINOpcodeVariant.POP),
	DUPLICATE_TOP(0x86, GameMakerWINOpcodeVariant.SINGLE_TYPE),
	RETURN(0x9C, GameMakerWINOpcodeVariant.SINGLE_TYPE),
	RETURN_VOID(0x9D, GameMakerWINOpcodeVariant.VOID),
	POP_TOP(0x9E, GameMakerWINOpcodeVariant.SINGLE_TYPE),
	GOTO(0xB6, GameMakerWINOpcodeVariant.GOTO),
	GOTO_CONDITIONAL(0xB7, GameMakerWINOpcodeVariant.GOTO),
	GOTO_INVERSE_CONDITIONAL(0xB8, GameMakerWINOpcodeVariant.GOTO),
	PUSH_ENVIRONMENT(0xBA, GameMakerWINOpcodeVariant.GOTO),
	POP_ENVIRONMENT(0xBB, GameMakerWINOpcodeVariant.GOTO),
	PUSH(0xC0, GameMakerWINOpcodeVariant.PUSH),
	PUSH_LOCAL(0xC1, GameMakerWINOpcodeVariant.PUSH),
	PUSH_GLOBAL(0xC2, GameMakerWINOpcodeVariant.PUSH),
	PUSH_BUILTIN(0xC3, GameMakerWINOpcodeVariant.PUSH),
	PUSH_SHORT(0x84, GameMakerWINOpcodeVariant.PUSH),
	CALL(0xD9, GameMakerWINOpcodeVariant.CALL),
	CALL_CLOSURE(0x99, GameMakerWINOpcodeVariant.CALL),
	BREAK(0xFF, GameMakerWINOpcodeVariant.BREAK);

	companion object {
		val mapping: Map<Int, GameMakerWINOpcode> = entries.associateBy(GameMakerWINOpcode::code)
	}
}