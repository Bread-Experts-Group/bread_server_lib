package org.bread_experts_group.coder.format.parse.elf.header

import org.bread_experts_group.coder.Flaggable

enum class ELFProgramHeaderFlags(override val position: Long) : Flaggable {
	PF_X(0x1),
	PF_W(0x2),
	PF_R(0x4)
}