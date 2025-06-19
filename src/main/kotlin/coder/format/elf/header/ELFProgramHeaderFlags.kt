package org.bread_experts_group.coder.format.elf.header

enum class ELFProgramHeaderFlags(val position: Int) {
	PF_X(0x1),
	PF_W(0x2),
	PF_R(0x4)
}