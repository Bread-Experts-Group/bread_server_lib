package org.bread_experts_group.api.computer.disc

import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.MappedEnumeration
import org.bread_experts_group.io.PrimitiveIOLayout

open class ISO9660VolumeDescriptor(
	val type: MappedEnumeration<UByte, ISO9660VolumeType>,
	val identifier: String = STANDARD_IDENTIFIER,
	val version: UByte,
	val data: ByteArray
) {
	companion object {
		const val STANDARD_IDENTIFIER = "CD001"
		val layout = PrimitiveIOLayout(
			{ r ->
				val type = ISO9660VolumeType.entries.id(r.u8())
				val identifier = r.get(5).toString(Charsets.ISO_8859_1)
				val version = r.u8()
				when (type.enum) {
					ISO9660VolumeType.BOOT_RECORD if version == 1u.toUByte() -> ISO9660BootRecord.layout.read(r)
					ISO9660VolumeType.PRIMARY if version == 1u.toUByte() -> ISO9660PrimaryVolume.layout.read(r)
					ISO9660VolumeType.SUPPLEMENTARY if version == 1u.toUByte() ->
						ISO9660SupplementaryVolume.layout.read(r)

					else -> ISO9660VolumeDescriptor(type, identifier, version, r.get(2041))
				}
			},
			{ w, i -> TODO("GAMMA") }
		)
	}

	override fun toString(): String = "ISO 9660 Volume Descriptor[v$version [\"${identifier}\"], " +
			"$type, data: ${data.size} bytes]"
}