package org.bread_experts_group.generic.protocol.vt100d

enum class VT100DEraseTypes(val n: Int) {
	CURSOR_AHEAD(0),
	CURSOR_BEHIND(1),
	ALL(2)
}