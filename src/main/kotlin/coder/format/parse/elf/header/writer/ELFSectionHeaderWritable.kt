package org.bread_experts_group.coder.format.parse.elf.header.writer

import org.bread_experts_group.coder.format.parse.elf.header.ELFSectionHeader
import org.bread_experts_group.coder.format.parse.elf.header.ELFSectionHeaderFlags

open class ELFSectionHeaderWritable(
	val name: String?,
	rawType: Int,
	flags: Set<ELFSectionHeaderFlags>,
	virtualAddress: Long,
	contentsPosition: Long,
	contentsLength: Long,
	link: Int,
	info: Int,
	alignment: Long,
	entrySize: Long,
	val contentsFileAbsolute: Boolean = false
) : ELFSectionHeader(
	rawType,
	flags.fold(0L) { acc, flag -> acc or flag.position },
	virtualAddress,
	contentsPosition,
	contentsLength,
	link,
	info,
	alignment,
	entrySize
)