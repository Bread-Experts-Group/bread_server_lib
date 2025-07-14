package org.bread_experts_group.coder.format.parse.elf

import org.bread_experts_group.coder.format.parse.CodingCompoundThrowable
import org.bread_experts_group.coder.format.parse.InvalidInputException
import org.bread_experts_group.coder.format.parse.Parser
import org.bread_experts_group.coder.format.parse.elf.header.*
import org.bread_experts_group.coder.format.parse.elf.header.writer.ELFContextuallyWritable
import org.bread_experts_group.stream.*
import java.io.FileInputStream
import java.io.InputStream

@OptIn(ExperimentalUnsignedTypes::class)
class ELFParser : Parser<Nothing?, ELFContextuallyWritable, FileInputStream>(
	"Executable and Linkable Format",
	FileInputStream::class
) {
	private val preread = ArrayDeque<ELFContextuallyWritable>()

	companion object {
		val goodSignature = ubyteArrayOf(0x7Fu, 0x45u, 0x4Cu, 0x46u).toByteArray()
	}

	override fun responsibleStream(of: ELFContextuallyWritable): FileInputStream = fqIn.from
	override fun readBase(compound: CodingCompoundThrowable): ELFContextuallyWritable = preread.removeFirstOrNull()
		?: throw FailQuickInputStream.EndOfStream()

	override fun inputInit() {
		val signature = fqIn.readNBytes(4)
		require(signature.contentEquals(goodSignature)) {
			"ELF signature mismatch; [${signature.toHexString()} =/= ${goodSignature.toHexString()}]"
		}

		val bitsRaw = fqIn.read()
		val bits = ELFHeaderBits.mapping[bitsRaw]
		if (bits == null) throw InvalidInputException("Unknown ELF bits [$bitsRaw]")
		val endianRaw = fqIn.read()
		val endian = ELFHeaderEndian.Companion.mapping[endianRaw]
		if (endian == null) throw InvalidInputException("Unknown ELF endian [$endianRaw]")

		fun Short.re() = if (endian == ELFHeaderEndian.LITTLE) this.le() else this
		fun Int.re() = if (endian == ELFHeaderEndian.LITTLE) this.le() else this
		fun Long.re() = if (endian == ELFHeaderEndian.LITTLE) this.le() else this
		fun InputStream.readBits() =
			if (bits == ELFHeaderBits.BIT_32) this.read32().re().toLong()
			else this.read64().re()

		val version = fqIn.read()
		if (version != 1) logger.warning("Unknown ELF version(1) [$version]")
		val abiRaw = fqIn.read()
		val abiVersion = fqIn.read()
		fqIn.skip(7)
		val objectTypeRaw = fqIn.read16().re().toInt()
		val isaRaw = fqIn.read16().re().toInt()
		val version2 = fqIn.read32().re()
		if (version2 != 1) logger.warning("Unknown ELF version(2) [$version2]")
		val entryPoint = fqIn.readBits().let { if (it == 0L) null else it }
		val programHeaderOffset = fqIn.readBits()
		val sectionHeaderOffset = fqIn.readBits()
		val isaFlags = fqIn.read32().re()
		fqIn.skip(2)
		val programHeaderEntrySize = fqIn.read16().re().toInt()
		val programHeaderEntries = fqIn.read16().re().toInt()
		val sectionHeaderEntrySize = fqIn.read16().re().toInt()
		val sectionHeaderEntries = fqIn.read16().re().toInt()
		val sectionNamesIndex = fqIn.read16().re().toInt()
		preread.add(
			ELFHeader(
				bits,
				endian,
				version,
				abiRaw,
				abiVersion,
				objectTypeRaw,
				isaRaw,
				version2,
				entryPoint,
				isaFlags
			)
		)
		fqIn.from.channel.position(programHeaderOffset)
		(0 until programHeaderEntries).forEach { _ ->
			val local = fqIn.readNBytes(programHeaderEntrySize).inputStream()
			val rawType = local.read32().re()
			if (bits == ELFHeaderBits.BIT_64) {
				val rawFlags = local.read32().re()
				val fileOffset = local.read64().re()
				val virtualAddress = local.read64().re()
				val physicalAddress = local.read64().re()
				val fileSize = local.read64().re()
				if (fileSize > Int.MAX_VALUE) throw NotImplementedError("Size too large!")
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
				if (fileSize > Int.MAX_VALUE) throw NotImplementedError("Size too large!")
				val memorySize = local.read32().re().toLong()
				val rawFlags = local.read32().re()
				val alignment = local.read32().re().toLong()
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
			}
		}
		fqIn.from.channel.position(sectionHeaderOffset)
		for (index in 0 until sectionHeaderEntries) {
			val local = fqIn.readNBytes(sectionHeaderEntrySize).inputStream()
			val nameOffset = local.read32().re()
			val rawType = local.read32().re()
			val rawFlags = local.readBits()
			val virtualAddress = local.readBits()
			val fileOffset = local.readBits()
			val fileSize = local.readBits()
			if (fileSize > Int.MAX_VALUE) throw NotImplementedError("Size too large!")
			preread.add(
				ELFWrittenSectionHeader(
					index == sectionNamesIndex,
					nameOffset,
					rawType,
					rawFlags,
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
}