package org.bread_experts_group.coder.format.parse.elf.header

import org.bread_experts_group.coder.Flaggable.Companion.from
import org.bread_experts_group.coder.Flaggable.Companion.raw
import org.bread_experts_group.coder.format.parse.InvalidInputException
import org.bread_experts_group.coder.format.parse.elf.header.writer.ELFContextuallyWritable
import org.bread_experts_group.coder.format.parse.elf.header.writer.ELFWriter
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
		rawType, flags.raw().toInt(),
		virtualAddress, physicalAddress, contentsPosition, contentsLength, memorySize, alignment,
		contentsFileAbsolute
	)

	override val tag: Nothing? = null

	val type = if (rawType >= ELFProgramHeaderType.PT_PROCESSOR_RAW.code)
		ELFProgramHeaderType.PT_PROCESSOR_RAW
	else if (rawType >= ELFProgramHeaderType.PT_OPERATING_SYSTEM_RAW.code)
		ELFProgramHeaderType.PT_OPERATING_SYSTEM_RAW
	else ELFProgramHeaderType.mapping[rawType]
		?: throw InvalidInputException("Unknown ELF program header entry type [$rawType]")

	val flags = ELFProgramHeaderFlags.entries.from(rawFlags)
	override fun toString(): String = "ELFProgramHeader[$type [$rawType], [${flags.joinToString(",")}] [$rawFlags], " +
			"f@$contentsPosition[$contentsLength], m@$virtualAddress/$physicalAddress[$memorySize], :$alignment]"

	context(stream: OutputStream, header: ELFHeader, dataPosition: Long)
	override fun write() {
		ELFWriter.Companion.w32(rawType)
		if (header.bits == ELFHeaderBits.BIT_32) {
			ELFWriter.Companion.w32((dataPosition + contentsPosition).toInt())
			ELFWriter.Companion.w32(virtualAddress.toInt())
			ELFWriter.Companion.w32(physicalAddress.toInt())
			ELFWriter.Companion.w32(contentsLength.toInt())
			ELFWriter.Companion.w32(memorySize.toInt())
			ELFWriter.Companion.w32(rawFlags)
			ELFWriter.Companion.w32(alignment.toInt())
		} else {
			ELFWriter.Companion.w32(rawFlags)
			ELFWriter.Companion.w64(dataPosition + contentsPosition)
			ELFWriter.Companion.w64(virtualAddress)
			ELFWriter.Companion.w64(physicalAddress)
			ELFWriter.Companion.w64(contentsLength)
			ELFWriter.Companion.w64(memorySize)
			ELFWriter.Companion.w64(alignment)
		}
	}
}