package org.bread_experts_group.coder.format.elf

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.elf.header.*
import org.bread_experts_group.stream.le
import org.bread_experts_group.stream.read16
import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.read64
import java.io.FileInputStream
import java.io.InputStream

@OptIn(ExperimentalUnsignedTypes::class)
class ELFInputStream(
	from: FileInputStream
) : Parser<Nothing?, ELFGeneralHeader, FileInputStream>("Executable and Linkable Format", from) {
	private val preread = ArrayDeque<ELFGeneralHeader>()

	init {
		val signature = from.readNBytes(4)
		val goodSignature = ubyteArrayOf(0x7Fu, 0x45u, 0x4Cu, 0x46u).toByteArray()
		require(signature.contentEquals(goodSignature)) {
			"ELF signature mismatch; [${signature.toHexString()} =/= ${goodSignature.toHexString()}]"
		}
	}

	init {
		val bitsRaw = from.read()
		val bits = ELFHeaderBits.mapping[bitsRaw]
		if (bits == null) throw DecodingException("Unknown ELF bits [$bitsRaw]")
		val endianRaw = from.read()
		val endian = ELFHeaderEndian.mapping[bitsRaw]
		if (endian == null) throw DecodingException("Unknown ELF endian [$endianRaw]")

		fun Short.re() = if (endian == ELFHeaderEndian.LITTLE) this.le() else this
		fun Int.re() = if (endian == ELFHeaderEndian.LITTLE) this.le() else this
		fun Long.re() = if (endian == ELFHeaderEndian.LITTLE) this.le() else this
		fun InputStream.readBits() =
			if (bits == ELFHeaderBits.BIT_32) this.read32().re().toLong()
			else this.read64().re()

		val version = from.read()
		if (version != 1) logger.warning("Unknown ELF version [$version]")
		val abiRaw = from.read()
		val abi = ELFApplicationBinaryInterface.mapping[abiRaw]
		if (abi == null) logger.warning("Unknown ELF ABI [$abiRaw]")
		val abiVersion = from.read()
		from.skip(7)
		val typeRaw = from.read16().re().toInt()
		val type =
			if (typeRaw >= ELFObjectType.PROCESSOR_RAW.code) ELFObjectType.PROCESSOR_RAW
			else if (typeRaw >= ELFObjectType.OPERATING_SYSTEM_RAW.code) ELFObjectType.OPERATING_SYSTEM_RAW
			else ELFObjectType.mapping[typeRaw]
		if (type == null) throw DecodingException("Unknown ELF object type [$typeRaw]")
		val isaRaw = from.read16().re().toInt()
		val isa = ELFInstructionSetArchitecture.mapping[isaRaw]
		if (isa == null) logger.warning("Unknown ELF ISA [$isaRaw]")
		val version2 = from.read32().re()
		val entry = from.readBits().let { if (it == 0L) null else it }
		val programHeaderOffset = from.readBits()
		val sectionHeaderOffset = from.readBits()
		val isaFlags = from.read32().re()
		from.skip(2)
		val programHeaderEntrySize = from.read16().re().toInt()
		val programHeaderEntries = from.read16().re().toInt()
		val sectionHeaderEntrySize = from.read16().re().toInt()
		val sectionHeaderEntries = from.read16().re().toInt()
		val sectionNamesIndex = from.read16().re().toInt()
		preread.add(
			ELFHeader(
				bits,
				endian,
				version,
				abi,
				abiRaw,
				abiVersion,
				type,
				typeRaw,
				isa,
				isaRaw,
				version2,
				entry,
				isaFlags,
				sectionNamesIndex
			)
		)
		from.channel.position(programHeaderOffset)
		(0 until programHeaderEntries).forEach { _ ->
			val local = from.readNBytes(programHeaderEntrySize).inputStream()
			val typeRaw = local.read32().re()
			val type =
				if (typeRaw >= ELFProgramHeaderType.PT_PROCESSOR_RAW.code)
					ELFProgramHeaderType.PT_PROCESSOR_RAW
				else if (typeRaw >= ELFProgramHeaderType.PT_OPERATING_SYSTEM_RAW.code)
					ELFProgramHeaderType.PT_OPERATING_SYSTEM_RAW
				else ELFProgramHeaderType.mapping[typeRaw]
			if (type == null) throw DecodingException("Unknown ELF program header entry type [$typeRaw]")
			if (bits == ELFHeaderBits.BIT_64) {
				val flagsRaw = local.read32().re()
				val flags = ELFProgramHeaderFlags.entries.filter { (flagsRaw and it.position) != 0 }.toSet()
				val fileOffset = local.read64().re()
				val virtualAddress = local.read64().re()
				val physicalAddress = local.read64().re()
				val fileSize = local.read64().re() // TODO read off
				val memorySize = local.read64().re()
				val alignment = local.read64().re()
				preread.add(
					ELFProgramHeader(
						type, typeRaw,
						flags, flagsRaw,
						fileOffset, virtualAddress, physicalAddress,
						byteArrayOf(), memorySize,
						if (alignment !in 0..1) alignment else null
					)
				)
			} else {
				val fileOffset = local.read32().re().toLong()
				val virtualAddress = local.read32().re().toLong()
				val physicalAddress = local.read32().re().toLong()
				val fileSize = local.read32().re().toLong() // TODO read off
				val memorySize = local.read32().re().toLong()
				val flagsRaw = local.read32().re()
				val flags = ELFProgramHeaderFlags.entries.filter { (flagsRaw and it.position) != 0 }.toSet()
				val alignment = local.read32().re().toLong()
				preread.add(
					ELFProgramHeader(
						type, typeRaw,
						flags, flagsRaw,
						fileOffset, virtualAddress, physicalAddress,
						byteArrayOf(), memorySize,
						if (alignment !in 0..1) alignment else null
					)
				)
			}
		}
		from.channel.position(sectionHeaderOffset)
		(0 until sectionHeaderEntries).forEach { _ ->
			val local = from.readNBytes(sectionHeaderEntrySize).inputStream()
			val nameOffset = local.read32().re()
			val typeRaw = local.read32().re()
			val type =
				if (typeRaw >= ELFSectionHeaderType.SHT_OPERATING_SYSTEM_RAW.code)
					ELFSectionHeaderType.SHT_OPERATING_SYSTEM_RAW
				else ELFSectionHeaderType.mapping[typeRaw]
			if (type == null) throw DecodingException("Unknown ELF section header entry type [$typeRaw]")
			val flagsRaw = local.readBits()
			val flags = ELFSectionHeaderFlags.entries.filter { (flagsRaw and it.position.toLong()) != 0L }.toSet()
			preread.add(
				ELFSectionHeader(
					nameOffset,
					type, typeRaw,
					flags,
					local.readBits(),
					local.readBits(),
					local.readBits(),
					local.read32().re(),
					local.read32().re(),
					local.readBits(),
					local.readBits()
				)
			)
		}
	}

	override fun responsibleStream(of: ELFGeneralHeader): FileInputStream = from
	override fun readBase(): ELFGeneralHeader = preread.removeFirstOrNull() ?: throw EndOfStream()
}