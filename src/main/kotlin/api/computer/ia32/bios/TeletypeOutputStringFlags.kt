package org.bread_experts_group.api.computer.ia32.bios

import org.bread_experts_group.coder.Flaggable

enum class TeletypeOutputStringFlags(override val position: Long) : Flaggable {
	UPDATE_CURSOR(0b00000001),
	ALTERNATING(0b00000010)
}