package org.bread_experts_group.coder.format.elf.header

enum class ELFHeaderEndian(val code: Int) {
	LITTLE(1),
	BIG(2);

	companion object {
		val mapping: Map<Int, ELFHeaderEndian> = entries.associateBy(ELFHeaderEndian::code)
	}
}