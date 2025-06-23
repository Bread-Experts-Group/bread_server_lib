package org.bread_experts_group.coder.format.elf.header

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.coder.format.elf.header.writer.ELFContextuallyWritable
import org.bread_experts_group.coder.format.elf.header.writer.ELFWriter.Companion.w32
import org.bread_experts_group.coder.format.elf.header.writer.ELFWriter.Companion.wBits
import java.io.OutputStream

abstract class ELFSectionHeader(
	val rawType: Int,
	val rawFlags: Long,
	val virtualAddress: Long,
	val contentsPosition: Long,
	val contentsLength: Long,
	val link: Int,
	val info: Int,
	alignment: Long,
	val entrySize: Long
) : ELFContextuallyWritable {
	override val tag: Nothing? = null
	val alignment = alignment.also {
		if (it != 0L && it.countOneBits() != 1) throw IllegalArgumentException("bad alignment [$it]")
	}

	val type = if (rawType >= ELFSectionHeaderType.SHT_OPERATING_SYSTEM_RAW.code)
		ELFSectionHeaderType.SHT_OPERATING_SYSTEM_RAW
	else ELFSectionHeaderType.mapping[rawType]
		?: throw DecodingException("Unknown ELF section header entry type [$rawType]")

	val flags = ELFSectionHeaderFlags.entries.filter { (rawFlags and it.position) != 0L }.toSet()
	override fun toString(): String = "ELFSectionHeader[$type [$rawType], " +
			"[${flags.joinToString(",")}], m@$virtualAddress, f@$contentsPosition[$contentsLength], $link | $info, " +
			"$alignment, #[$entrySize]]"

	context(stream: OutputStream, header: ELFHeader, dataPosition: Long)
	override fun write() {
		w32(rawType)
		wBits(rawFlags)
		wBits(virtualAddress)
		wBits(dataPosition + contentsPosition)
		wBits(contentsLength)
		w32(link)
		w32(info)
		wBits(alignment)
		wBits(entrySize)
	}
}