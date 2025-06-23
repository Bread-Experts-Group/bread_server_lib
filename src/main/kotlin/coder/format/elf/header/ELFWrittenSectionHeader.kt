package org.bread_experts_group.coder.format.elf.header

class ELFWrittenSectionHeader(
	val sectionNames: Boolean,
	val nameOffset: Int,
	rawType: Int,
	rawFlags: Long,
	virtualAddress: Long,
	fileOffset: Long,
	fileSize: Long,
	link: Int,
	info: Int,
	alignment: Long,
	entrySize: Long
) : ELFSectionHeader(
	rawType, rawFlags,
	virtualAddress, fileOffset, fileSize,
	link, info, alignment, entrySize
)