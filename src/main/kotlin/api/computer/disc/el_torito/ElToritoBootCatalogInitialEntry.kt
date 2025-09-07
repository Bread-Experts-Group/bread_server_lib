package org.bread_experts_group.api.computer.disc.el_torito

import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.MappedEnumeration
import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.SequentialIOLayout

class ElToritoBootCatalogInitialEntry(
	val bootable: Boolean,
	val bootMediaType: MappedEnumeration<UByte, ElToritoBootMediaType>,
	val loadSegment: UShort,
	val systemType: UByte,
	val sectorCount: UShort,
	val loadRBA: UInt
) {
	companion object {
		fun decode(
			bootable: UByte,
			bootMedia: UByte,
			loadSegment: UShort,
			systemType: UByte,
			sectorCount: UShort,
			loadRBA: UInt
		): ElToritoBootCatalogInitialEntry {
			if (bootable != 0x88.toUByte() && bootable != 0x00.toUByte())
				throw IllegalArgumentException("Bad boot indicator [$bootable]")
			return ElToritoBootCatalogInitialEntry(
				bootable == 0x88.toUByte(),
				ElToritoBootMediaType.entries.id(bootMedia),
				if (loadSegment == 0.toUShort()) 0x7C0.toUShort() else loadSegment,
				systemType, sectorCount, loadRBA
			)
		}

		val layout = SequentialIOLayout(
			::decode,
			IOLayout.UNSIGNED_BYTE,
			IOLayout.UNSIGNED_BYTE,
			IOLayout.UNSIGNED_SHORT,
			IOLayout.UNSIGNED_BYTE,
			IOLayout.UNSIGNED_BYTE.padding(),
			IOLayout.UNSIGNED_SHORT,
			IOLayout.UNSIGNED_INT,
			IOLayout.UNSIGNED_BYTE.padding()
		)
	}

	override fun toString(): String = "El Torito Initial Entry [${if (bootable) "" else "not"} bootable, " +
			"$bootMediaType]" +
			"\n\tLoad Segment: ${loadSegment.toHexString()}" +
			"\n\tSystem Type: ${systemType.toHexString()}" +
			"\n\tSector Count: $sectorCount" +
			"\n\tLoad RBA: ${loadRBA.toHexString()}"
}