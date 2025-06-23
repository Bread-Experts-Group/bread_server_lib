package org.bread_experts_group.coder.format.elf

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.elf.header.*
import org.bread_experts_group.coder.format.elf.header.writer.ELFContextuallyWritable
import org.bread_experts_group.stream.le
import org.bread_experts_group.stream.read16
import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.read64
import java.io.FileInputStream
import java.io.InputStream

@OptIn(ExperimentalUnsignedTypes::class)
class ELFInputStream(
	from: FileInputStream
) : Parser<Nothing?, ELFContextuallyWritable, FileInputStream>("Executable and Linkable Format", from) {
	private val preread = ArrayDeque<ELFContextuallyWritable>()

	companion object {
		val goodSignature = ubyteArrayOf(0x7Fu, 0x45u, 0x4Cu, 0x46u).toByteArray()
	}

	init {
		val signature = from.readNBytes(4)
		require(signature.contentEquals(goodSignature)) {
			"ELF signature mismatch; [${signature.toHexString()} =/= ${goodSignature.toHexString()}]"
		}
	}

	init {
		val bitsRaw = from.read()
		val bits = ELFHeaderBits.mapping[bitsRaw]
		if (bits == null) throw DecodingException("Unknown ELF bits [$bitsRaw]")
		val endianRaw = from.read()
		val endian = ELFHeaderEndian.mapping[endianRaw]
		if (endian == null) throw DecodingException("Unknown ELF endian [$endianRaw]")

		fun Short.re() = if (endian == ELFHeaderEndian.LITTLE) this.le() else this
		fun Int.re() = if (endian == ELFHeaderEndian.LITTLE) this.le() else this
		fun Long.re() = if (endian == ELFHeaderEndian.LITTLE) this.le() else this
		fun InputStream.readBits() =
			if (bits == ELFHeaderBits.BIT_32) this.read32().re().toLong()
			else this.read64().re()

		val version = from.read()
		if (version != 1) logger.warning("Unknown ELF version(1) [$version]")
		val abiRaw = from.read()
		val abiVersion = from.read()
		from.skip(7)
		val typeRaw = from.read16().re().toInt()
		val isaRaw = from.read16().re().toInt()
		val version2 = from.read32().re()
		if (version2 != 1) logger.warning("Unknown ELF version(2) [$version2]")
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
				abiRaw,
				abiVersion,
				typeRaw,
				isaRaw,
				version2,
				entry,
				isaFlags
			)
		)
		from.channel.position(programHeaderOffset)
		(0 until programHeaderEntries).forEach { _ ->
			val local = from.readNBytes(programHeaderEntrySize).inputStream()
			val rawType = local.read32().re()
			if (bits == ELFHeaderBits.BIT_64) {
				val rawFlags = local.read32().re()
				val fileOffset = local.read64().re()
				val virtualAddress = local.read64().re()
				val physicalAddress = local.read64().re()
				val fileSize = local.read64().re()
				if (fileSize > Int.MAX_VALUE) throw DecodingException("Size too large!")
				val memorySize = local.read64().re()
				val alignment = local.read64().re()
				preread.add(
					ELFProgramHeader(
						rawType,
						rawFlags,
						virtualAddress,
						physicalAddress,
						fileOffset,
						fileSize,
						memorySize,
						alignment
					)
				)
			} else {
				val fileOffset = local.read32().re().toLong()
				val virtualAddress = local.read32().re().toLong()
				val physicalAddress = local.read32().re().toLong()
				val fileSize = local.read32().re().toLong()
				if (fileSize > Int.MAX_VALUE) throw DecodingException("Size too large!")
				val memorySize = local.read32().re().toLong()
				val flagsRaw = local.read32().re()
				val alignment = local.read32().re().toLong()
				preread.add(
					ELFProgramHeader(
						rawType,
						flagsRaw,
						virtualAddress,
						physicalAddress,
						fileOffset,
						fileSize,
						memorySize,
						alignment
					)
				)
			}
		}
		from.channel.position(sectionHeaderOffset)
		for (index in 0 until sectionHeaderEntries) {
			val local = from.readNBytes(sectionHeaderEntrySize).inputStream()
			val nameOffset = local.read32().re()
			val typeRaw = local.read32().re()
			val flagsRaw = local.readBits()
			val virtualAddress = local.readBits()
			val fileOffset = local.readBits()
			val fileSize = local.readBits()
			if (fileSize > Int.MAX_VALUE) throw DecodingException("Size too large!")
			preread.add(
				ELFWrittenSectionHeader(
					index == sectionNamesIndex,
					nameOffset,
					typeRaw,
					flagsRaw,
					virtualAddress,
					fileOffset,
					fileSize,
					local.read32().re(),
					local.read32().re(),
					local.readBits(),
					local.readBits()
				)
			)
		}
	}

	override fun responsibleStream(of: ELFContextuallyWritable): FileInputStream = from
	override fun readBase(): ELFContextuallyWritable = preread.removeFirstOrNull() ?: throw EndOfStream()
}