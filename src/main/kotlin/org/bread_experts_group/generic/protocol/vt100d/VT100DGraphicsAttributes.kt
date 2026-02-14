package org.bread_experts_group.generic.protocol.vt100d

import org.bread_experts_group.generic.Mappable

enum class VT100DGraphicsAttributes(override val id: UInt) : Mappable<VT100DGraphicsAttributes, UInt> {
	DEFAULT(0u),
	INTENSE(1u),
	NO_INTENSE(22u),
	UNDERLINE(4u),
	NO_UNDERLINE(24u),
	INVERTED(7u),
	NO_INVERTED(27u),
	FG_BLACK(30u),
	FG_RED(31u),
	FG_GREEN(32u),
	FG_YELLOW(33u),
	FG_BLUE(34u),
	FG_MAGENTA(35u),
	FG_CYAN(36u),
	FG_WHITE(37u),
	FG_DEFAULT(39u),
	BG_BLACK(40u),
	BG_RED(41u),
	BG_GREEN(42u),
	BG_YELLOW(43u),
	BG_BLUE(44u),
	BG_MAGENTA(45u),
	BG_CYAN(46u),
	BG_WHITE(47u),
	BG_DEFAULT(49u),
	INTENSE_FG_BLACK(90u),
	INTENSE_FG_RED(91u),
	INTENSE_FG_GREEN(92u),
	INTENSE_FG_YELLOW(93u),
	INTENSE_FG_BLUE(94u),
	INTENSE_FG_MAGENTA(95u),
	INTENSE_FG_CYAN(96u),
	INTENSE_FG_WHITE(97u),
	INTENSE_BG_BLACK(100u),
	INTENSE_BG_RED(101u),
	INTENSE_BG_GREEN(102u),
	INTENSE_BG_YELLOW(103u),
	INTENSE_BG_BLUE(104u),
	INTENSE_BG_MAGENTA(105u),
	INTENSE_BG_CYAN(106u),
	INTENSE_BG_WHITE(107u);

	override fun toString(): String = stringForm()
	override val tag: String = name
}