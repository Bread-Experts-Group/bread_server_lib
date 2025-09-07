package org.bread_experts_group.api.computer.disc

import org.bread_experts_group.api.computer.disc.ISO9660PrimaryVolume.Companion.dateTimeLayout
import org.bread_experts_group.coder.MappedEnumeration
import org.bread_experts_group.io.IOEndian
import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.SequencedIOLayout.Companion.array
import org.bread_experts_group.io.SequencedIOLayout.Companion.isoLatin1
import org.bread_experts_group.io.SequentialIOLayout
import java.time.ZonedDateTime

class ISO9660SupplementaryVolume(
	val systemIdentifier: String,
	val volumeIdentifier: String,
	val volumeSpaceSize: UInt,
	val escapeSequences: ByteArray,
	val volumeSetSize: UShort,
	val volumeSequence: UShort,
	val logicalBlockSize: UShort,
	val pathTableSize: UInt,
	val pathTableTypeLOffset: UInt,
	val pathTableTypeLOffsetOptional: UInt?,
	val pathTableTypeMOffset: UInt,
	val pathTableTypeMOffsetOptional: UInt?,
	val volumeSetIdentifier: String,
	val publisherIdentifier: String,
	val dataPreparerIdentifier: String,
	val applicationIdentifier: String,
	val copyrightFileIdentifier: String,
	val abstractIdentifier: String,
	val bibliographyFileIdentifier: String,
	val volumeCreationDateTime: ZonedDateTime?,
	val volumeModificationDateTime: ZonedDateTime?,
	val volumeExpirationDateTime: ZonedDateTime?,
	val volumeEffectiveDateTime: ZonedDateTime?,
	val fileStructureVersion: UByte,
	val applicationData: ByteArray
) : ISO9660VolumeDescriptor(
	MappedEnumeration(ISO9660VolumeType.PRIMARY),
	version = 1u,
	data = run {
		ByteArray(2041) // TODO.
	}
) {
	companion object {
		val layout = SequentialIOLayout(
			::ISO9660SupplementaryVolume,
			IOLayout.BYTE.padding(),
			IOLayout.CHAR.sequence(32).isoLatin1(),
			IOLayout.CHAR.sequence(32).isoLatin1(),
			IOLayout.BYTE.sequence(8).padding(),
			IOLayout.UNSIGNED_INT.order(IOEndian.BOTH_LE_BE),
			IOLayout.BYTE.sequence(32).array(),
			IOLayout.UNSIGNED_SHORT.order(IOEndian.BOTH_LE_BE),
			IOLayout.UNSIGNED_SHORT.order(IOEndian.BOTH_LE_BE),
			IOLayout.UNSIGNED_SHORT.order(IOEndian.BOTH_LE_BE),
			IOLayout.UNSIGNED_INT.order(IOEndian.BOTH_LE_BE),
			IOLayout.UNSIGNED_INT.order(IOEndian.LITTLE),
			IOLayout.UNSIGNED_INT.order(IOEndian.LITTLE),
			IOLayout.UNSIGNED_INT.order(IOEndian.BIG),
			IOLayout.UNSIGNED_INT.order(IOEndian.BIG),
			IOLayout.BYTE.sequence(34).padding(),
			IOLayout.CHAR.sequence(128).isoLatin1(),
			IOLayout.CHAR.sequence(128).isoLatin1(),
			IOLayout.CHAR.sequence(128).isoLatin1(),
			IOLayout.CHAR.sequence(128).isoLatin1(),
			IOLayout.CHAR.sequence(37).isoLatin1(),
			IOLayout.CHAR.sequence(37).isoLatin1(),
			IOLayout.CHAR.sequence(37).isoLatin1(),
			dateTimeLayout,
			dateTimeLayout,
			dateTimeLayout,
			dateTimeLayout,
			IOLayout.UNSIGNED_BYTE,
			IOLayout.BYTE.sequence(1).padding(),
			IOLayout.BYTE.sequence(512).array(),
			IOLayout.BYTE.sequence(653).padding(),
		)
	}

	override fun toString(): String = super.toString() +
			"\n\tSupplementary Volume [\"${systemIdentifier}\" / " +
			"\"${volumeIdentifier}\": $volumeSpaceSize bytes, $volumeSetSize set size, " +
			"#$volumeSequence]" +
			"\n\t\tEscape Sequences: ${escapeSequences.toHexString()}" +
			"\n\t\tLogical Block Size: $logicalBlockSize bytes" +
			"\n\t\tPath Table" +
			"\n\t\t\tSize: $pathTableSize bytes" +
			"\n\t\t\tType L: [Offset: $pathTableTypeLOffset, Optional Offset: $pathTableTypeLOffsetOptional]" +
			"\n\t\t\tType M: [Offset: $pathTableTypeMOffset, Optional Offset: $pathTableTypeMOffsetOptional]" +
			"\n\t\tVolume Set        Identifier: \"${volumeSetIdentifier}\"" +
			"\n\t\tPublisher         Identifier: \"${publisherIdentifier}\"" +
			"\n\t\tData Preparer     Identifier: \"${dataPreparerIdentifier}\"" +
			"\n\t\tApplication       Identifier: \"${applicationIdentifier}\"" + "" +
			"\n\t\tCopyright    File Identifier: \"${copyrightFileIdentifier}\"" +
			"\n\t\tAbstract     File Identifier: \"${abstractIdentifier}\"" +
			"\n\t\tBibliography File Identifier: \"${bibliographyFileIdentifier}\"" +
			"\n\t\tCreated   At: $volumeCreationDateTime" +
			"\n\t\tModified  At: $volumeModificationDateTime" +
			"\n\t\tExpires   At: $volumeExpirationDateTime" +
			"\n\t\tEffective At: $volumeEffectiveDateTime" +
			"\n\t\tFile Structure v$fileStructureVersion" +
			"\n\t\t${applicationData.size} bytes Application Data"
}