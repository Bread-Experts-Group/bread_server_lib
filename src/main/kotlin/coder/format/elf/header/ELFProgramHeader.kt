package org.bread_experts_group.coder.format.elf.header

class ELFProgramHeader(
	val type: ELFProgramHeaderType,
	val typeRaw: Int,
	val flags: Set<ELFProgramHeaderFlags>,
	val flagsRaw: Int,
	val fileOffset: Long,
	val virtualAddress: Long,
	val physicalAddress: Long,
	val contents: ByteArray,
	val memorySize: Long,
	val alignment: Long?
) : ELFGeneralHeader() {
	override fun toString(): String = "ELFProgramHeader[$type [$typeRaw], [${flags.joinToString(",")}] [$flagsRaw], " +
			"f@$fileOffset[${contents.size}], m@$virtualAddress/$physicalAddress[$memorySize], :$alignment]"
}