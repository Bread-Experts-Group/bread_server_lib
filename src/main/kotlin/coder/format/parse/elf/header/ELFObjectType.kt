package org.bread_experts_group.coder.format.parse.elf.header

enum class ELFObjectType(val code: Int) {
	ET_NONE(0x00),
	ET_REL(0x01),
	ET_EXEC(0x02),
	ET_DYN(0x03),
	ET_CORE(0x04),
	OPERATING_SYSTEM_RAW(0xFE00),
	PROCESSOR_RAW(0xFF00);

	companion object {
		val mapping: Map<Int, ELFObjectType> = entries.associateBy(ELFObjectType::code)
	}
}