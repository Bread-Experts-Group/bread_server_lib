package org.bread_experts_group.coder.format.elf.header

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.coder.format.elf.header.writer.ELFContextuallyWritable
import org.bread_experts_group.coder.format.elf.header.writer.ELFWriter.Companion.w32
import org.bread_experts_group.coder.format.elf.header.writer.ELFWriter.Companion.w64
import java.io.OutputStream

class ELFProgramHeader(
	val rawType: Int,
	val rawFlags: Int,
	val virtualAddress: Long,
	val physicalAddress: Long,
	val contentsPosition: Long,
	val contentsLength: Long,
	val memorySize: Long,
	alignment: Long,
	val contentsFileAbsolute: Boolean = false
) : ELFContextuallyWritable {
	val alignment = alignment.also {
		if (it != 0L && it.countOneBits() != 1) throw IllegalArgumentException("bad alignment [$it]")
	}

	constructor(
		rawType: Int,
		flags: Set<ELFProgramHeaderFlags>,
		virtualAddress: Long,
		physicalAddress: Long,
		contentsPosition: Long,
		contentsLength: Long,
		memorySize: Long,
		alignment: Long,
		contentsFileAbsolute: Boolean = false
	) : this(
		rawType, flags.fold(0) { acc, flag -> acc or flag.position },
		virtualAddress, physicalAddress, contentsPosition, contentsLength, memorySize, alignment,
		contentsFileAbsolute
	)

	override val tag: Nothing? = null

	val type = if (rawType >= ELFProgramHeaderType.PT_PROCESSOR_RAW.code)
		ELFProgramHeaderType.PT_PROCESSOR_RAW
	else if (rawType >= ELFProgramHeaderType.PT_OPERATING_SYSTEM_RAW.code)
		ELFProgramHeaderType.PT_OPERATING_SYSTEM_RAW
	else ELFProgramHeaderType.mapping[rawType]
		?: throw DecodingException("Unknown ELF program header entry type [$rawType]")

	val flags = ELFProgramHeaderFlags.entries.filter { (rawFlags and it.position) != 0 }.toSet()
	override fun toString(): String = "ELFProgramHeader[$type [$rawType], [${flags.joinToString(",")}] [$rawFlags], " +
			"f@$contentsPosition[$contentsLength], m@$virtualAddress/$physicalAddress[$memorySize], :$alignment]"

	context(stream: OutputStream, header: ELFHeader, dataPosition: Long)
	override fun write() {
		w32(rawType)
		if (header.bits == ELFHeaderBits.BIT_32) {
			w32((dataPosition + contentsPosition).toInt())
			w32(virtualAddress.toInt())
			w32(physicalAddress.toInt())
			w32(contentsLength.toInt())
			w32(memorySize.toInt())
			w32(rawFlags)
			w32(alignment.toInt())
		} else {
			w32(rawFlags)
			w64(dataPosition + contentsPosition)
			w64(virtualAddress)
			w64(physicalAddress)
			w64(contentsLength)
			w64(memorySize)
			w64(alignment)
		}
	}
}