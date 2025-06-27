package org.bread_experts_group.coder.format.elf.header.writer

import org.bread_experts_group.coder.format.DiscreteWriter
import org.bread_experts_group.coder.format.elf.ELFParser
import org.bread_experts_group.coder.format.elf.header.*
import org.bread_experts_group.stream.*
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import kotlin.math.max

class ELFWriter(private val header: ELFHeader) : DiscreteWriter() {
	companion object {
		context(stream: OutputStream, header: ELFHeader)
		fun w16(n: Int) = n.toShort().let {
			stream.write16(if (header.endian == ELFHeaderEndian.LITTLE) it.le() else it)
		}

		context(stream: OutputStream, header: ELFHeader)
		fun w32(n: Int) = stream.write32(if (header.endian == ELFHeaderEndian.LITTLE) n.le() else n)

		context(stream: OutputStream, header: ELFHeader)
		fun w64(n: Long) = stream.write64(if (header.endian == ELFHeaderEndian.LITTLE) n.le() else n)

		context(stream: OutputStream, header: ELFHeader)
		fun wBits(n: Long) = if (header.bits == ELFHeaderBits.BIT_64) w64(n) else w32(n.toInt())
	}

	val programHeaders = mutableListOf<ELFProgramHeader>()
	val sectionHeaders = mutableListOf<ELFSectionHeaderWritable>()
	var data = ByteArray(0)

	override fun writeFull(stream: OutputStream) = context(stream, header) {
		stream.write(ELFParser.Companion.goodSignature)
		stream.write(header.bits.code)
		stream.write(header.endian.code)
		stream.write(header.version)
		stream.write(header.abiRaw)
		stream.write(header.abiVersion)
		stream.write(ByteArray(7))
		w16(header.objectTypeRaw)
		w16(header.isaRaw)
		w32(header.version2)
		wBits(header.entryPoint ?: 0)
		val programHeaderStart = if (header.bits == ELFHeaderBits.BIT_64) 0x40L else 0x34L
		wBits(programHeaderStart)
		val programHeaderEntrySize = if (header.bits == ELFHeaderBits.BIT_64) 0x38 else 0x20
		val programHeaderLength = programHeaderEntrySize * (programHeaders.size)
//		programHeaders.addFirst(
//			ELFProgramHeader(
//				ELFProgramHeaderType.PT_PROGRAM_HEADER.code,
//				setOf(ELFProgramHeaderFlags.PF_R),
//				programHeaderStart,
//				programHeaderStart,
//				programHeaderStart,
//				programHeaderLength.toLong(),
//				programHeaderLength.toLong(),
//				8,
//				true
//			)
//		)
//		programHeaders.add(
//			1,
//			ELFProgramHeader(
//				ELFProgramHeaderType.PT_LOAD.code,
//				setOf(ELFProgramHeaderFlags.PF_R),
//				0,
//				0,
//				0,
//				programHeaderStart + programHeaderLength,
//				programHeaderStart + programHeaderLength,
//				8,
//				true
//			)
//		)
		val sectionHeaderStart = programHeaderStart + programHeaderLength
		wBits(if (sectionHeaders.isNotEmpty()) sectionHeaderStart else 0)
		w32(header.isaFlags)
		w16(programHeaderStart.toInt())
		w16(programHeaderEntrySize)
		w16(programHeaders.size)
		val sectionHeaderEntrySize = if (header.bits == ELFHeaderBits.BIT_64) 0x40 else 0x28
		w16(sectionHeaderEntrySize)
		sectionHeaders.addFirst(
			ELFSectionHeaderWritable(
				"",
				ELFSectionHeaderType.UNUSED.code,
				setOf(),
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				true
			)
		)
		val (strings, offsets) = ByteArrayOutputStream().use {
			val offsets = mutableMapOf<String, Int>()
			(sectionHeaders.mapNotNull { section -> section.name } + ".shstrtab").forEach { name ->
				offsets[name] = it.size()
				it.writeString(name)
				it.write(0)
			}
			it.toByteArray() to offsets
		}
		val stringsPosition = data.size
		data = data + strings
		sectionHeaders.add(
			ELFSectionHeaderWritable(
				".shstrtab",
				ELFSectionHeaderType.SHT_STRTAB.code,
				setOf(),
				0,
				stringsPosition.toLong(),
				strings.size.toLong(),
				0,
				0,
				1,
				0
			)
		)
		w16(sectionHeaders.size)
		w16(max(0, sectionHeaders.lastIndex))
		val dataStart = sectionHeaderStart + (sectionHeaderEntrySize * sectionHeaders.size)
		programHeaders.forEach {
			context(if (it.contentsFileAbsolute) 0 else dataStart) { it.write() }
		}
		sectionHeaders.forEach {
			w32(if (it.name != null) offsets.getValue(it.name) else 0)
			context(if (it.contentsFileAbsolute) 0 else dataStart) { it.write() }
		}
		stream.write(data)
	}
}