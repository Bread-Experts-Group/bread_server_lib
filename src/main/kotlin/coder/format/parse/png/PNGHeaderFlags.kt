package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Flaggable

enum class PNGHeaderFlags(override val position: Long) : Flaggable {
	PALETTE(0x1),
	TRUE_COLOR(0x2),
	ALPHA(0x4)
}