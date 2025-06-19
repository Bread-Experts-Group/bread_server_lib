package org.bread_experts_group.coder.format.elf.header

import org.bread_experts_group.hex

class ELFHeader(
	val bits: ELFHeaderBits,
	val endian: ELFHeaderEndian,
	val version: Int,
	val abi: ELFApplicationBinaryInterface?,
	val abiRaw: Int,
	val abiVersion: Int,
	val objectType: ELFObjectType,
	val objectTypeRaw: Int,
	val isa: ELFInstructionSetArchitecture?,
	val isaRaw: Int,
	val version2: Int,
	val entryPoint: Long?,
	val isaFlags: Int,
	val sectionNamesSectionIndex: Int
) : ELFGeneralHeader() {
	override fun toString(): String = "ELFHeader[$bits, $endian, $version.$version2, $abi [$abiRaw] $abiVersion, " +
			"$objectType [$objectTypeRaw], $isa [$isaRaw] $isaFlags" +
			(if (entryPoint != null) ", @${hex(entryPoint.toULong())}" else "") + ']'
}