package org.bread_experts_group.coder.format.parse.elf.header

enum class ELFProgramHeaderType(val code: Int) {
	PT_NULL(0x00000000),
	PT_LOAD(0x00000001),
	PT_DYNAMIC(0x00000002),
	PT_INTERP(0x00000003),
	PT_NOTE(0x00000004),
	PT_SHLIB(0x00000005),
	PT_PROGRAM_HEADER(0x00000006),
	PT_TLS(0x00000007),
	PT_OPERATING_SYSTEM_RAW(0x60000000),
	PT_PROCESSOR_RAW(0x70000000);

	companion object {
		val mapping = entries.associateBy(ELFProgramHeaderType::code)
	}
}