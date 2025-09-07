package org.bread_experts_group.api.computer.disc.el_torito

import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.MappedEnumeration
import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.SequencedIOLayout.Companion.isoLatin1
import org.bread_experts_group.io.SequentialIOLayout

data class ElToritoBootCatalogValidationEntry(
	val platform: MappedEnumeration<UByte, ElToritoPlatform>,
	val cdDeveloper: String,
	val checksum: UShort
) {
	companion object {
		fun decode(
			header: UByte,
			platform: UByte,
			reserved: UShort,
			cdDeveloper: String,
			checksum: UShort,
			kb55: UByte,
			kbAA: UByte
		): ElToritoBootCatalogValidationEntry {
			if (header != 1.toUByte()) throw IllegalArgumentException("Invalid header [$header]")
			if (kb55 != 0x55.toUByte()) throw IllegalArgumentException("Invalid key byte 0 [$kb55]")
			if (kbAA != 0xAA.toUByte()) throw IllegalArgumentException("Invalid key byte 1 [$kbAA]")
			if (reserved != 0.toUShort()) throw IllegalArgumentException("Invalid reserved bytes [$reserved]")
			return ElToritoBootCatalogValidationEntry(
				ElToritoPlatform.entries.id(platform),
				cdDeveloper, checksum
			)
		}

		val layout = SequentialIOLayout(
			::decode,
			IOLayout.UNSIGNED_BYTE,
			IOLayout.UNSIGNED_BYTE,
			IOLayout.UNSIGNED_SHORT,
			IOLayout.CHAR.sequence(24).isoLatin1(),
			IOLayout.UNSIGNED_SHORT,
			IOLayout.UNSIGNED_BYTE,
			IOLayout.UNSIGNED_BYTE
		)
	}

	override fun toString(): String = "El Torito Validation Entry [$platform: \"$cdDeveloper\"]" +
			":${checksum.toHexString()}"
}