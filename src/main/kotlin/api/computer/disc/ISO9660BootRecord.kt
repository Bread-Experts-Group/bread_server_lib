package org.bread_experts_group.api.computer.disc

import org.bread_experts_group.coder.MappedEnumeration
import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.SequencedIOLayout.Companion.array
import org.bread_experts_group.io.SequencedIOLayout.Companion.isoLatin1
import org.bread_experts_group.io.SequentialIOLayout

class ISO9660BootRecord(
	val bootSystemIdentifier: String,
	val bootIdentifier: String,
	val bootData: ByteArray
) : ISO9660VolumeDescriptor(
	MappedEnumeration(ISO9660VolumeType.BOOT_RECORD),
	version = 1u,
	data = run {
		byteArrayOf() // TODO data
	}
) {
	companion object {
		val layout = SequentialIOLayout(
			::ISO9660BootRecord,
			IOLayout.CHAR.sequence(32).isoLatin1(),
			IOLayout.CHAR.sequence(32).isoLatin1(),
			IOLayout.BYTE.sequence(1977).array()
		)
	}

	override fun toString(): String = super.toString() +
			"\n\tBoot Record [\"${bootSystemIdentifier}\" / " +
			"\"${bootIdentifier}\": ${bootData.size} bytes]"
}