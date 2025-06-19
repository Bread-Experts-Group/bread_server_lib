package org.bread_experts_group.coder.format.elf.header

enum class ELFHeaderEndian(val code: Int) {
	BIG(1),
	LITTLE(2);

	companion object {
		val mapping: Map<Int, ELFHeaderEndian> = entries.associateBy(ELFHeaderEndian::code)
	}
}