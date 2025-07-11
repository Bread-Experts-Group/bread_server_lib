package org.bread_experts_group.coder.format.parse.elf.header

import org.bread_experts_group.coder.format.parse.elf.header.writer.ELFContextuallyWritable
import org.bread_experts_group.hex
import java.io.OutputStream

class ELFHeader(
	val bits: ELFHeaderBits,
	val endian: ELFHeaderEndian,
	val version: Int,
	val abiRaw: Int,
	val abiVersion: Int,
	val objectTypeRaw: Int,
	val isaRaw: Int,
	val version2: Int,
	val entryPoint: Long?,
	val isaFlags: Int
) : ELFContextuallyWritable {
	override val tag: Nothing? = null

	val abi = ELFApplicationBinaryInterface.Companion.mapping[abiRaw]
	val objectType = ELFObjectType.Companion.mapping[objectTypeRaw]
	val isa = ELFInstructionSetArchitecture.Companion.mapping[isaRaw]

	override fun toString(): String = "ELFHeader[$bits, $endian, $version.$version2, $abi [$abiRaw] $abiVersion, " +
			"$objectType [$objectTypeRaw], $isa [$isaRaw] $isaFlags" +
			(if (entryPoint != null) ", @${hex(entryPoint.toULong())}" else "") + ']'

	context(stream: OutputStream, header: ELFHeader, dataPosition: Long)
	override fun write() {
		TODO("Not yet implemented")
	}
}