package org.bread_experts_group.coder.format.parse.elf.header

enum class ELFSectionHeaderType(val code: Int) {
	UNUSED(0x0),
	SHT_PROGBITS(0x1),
	SHT_SYMTAB(0x2),
	SHT_STRTAB(0x3),
	SHT_RELA(0x4),
	SHT_HASH(0x5),
	SHT_DYNAMIC(0x6),
	SHT_NOTE(0x7),
	SHT_NOBITS(0x8),
	SHT_REL(0x9),
	SHT_SHLIB(0x0A),
	SHT_DYNSYM(0x0B),
	SHT_INIT_ARRAY(0x0E),
	SHT_FINI_ARRAY(0x0F),
	SHT_PREINIT_ARRAY(0x10),
	SHT_GROUP(0x11),
	SHT_SYMTAB_SHNDX(0x12),
	SHT_NUM(0x13),
	SHT_OPERATING_SYSTEM_RAW(0x60000000);

	companion object {
		val mapping = entries.associateBy(ELFSectionHeaderType::code)
	}
}