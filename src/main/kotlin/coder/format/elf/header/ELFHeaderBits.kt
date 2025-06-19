package org.bread_experts_group.coder.format.elf.header

enum class ELFHeaderBits(val code: Int) {
	BIT_32(1),
	BIT_64(2);

	companion object {
		val mapping: Map<Int, ELFHeaderBits> = entries.associateBy(ELFHeaderBits::code)
	}
}