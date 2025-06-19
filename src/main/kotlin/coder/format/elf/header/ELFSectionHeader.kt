package org.bread_experts_group.coder.format.elf.header

class ELFSectionHeader(
	val nameOffset: Int,
	val type: ELFSectionHeaderType,
	val typeRaw: Int,
	val flags: Set<ELFSectionHeaderFlags>,
	val virtualAddress: Long,
	val fileOffset: Long,
	val fileSize: Long,
	val link: Int,
	val info: Int,
	val alignment: Long,
	val entrySize: Long
) : ELFGeneralHeader() {
	override fun toString(): String = "ELFSectionHeader[@$nameOffset, $type [$typeRaw], " +
			"[${flags.joinToString(",")}], m@$virtualAddress, f@$fileOffset[$fileSize], $link | $info, $alignment, " +
			"#[$entrySize]]"
}