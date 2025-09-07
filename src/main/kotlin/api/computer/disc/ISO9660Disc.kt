package org.bread_experts_group.api.computer.disc

import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.PrimitiveIOLayout
import org.bread_experts_group.io.SequencedIOLayout.Companion.array
import org.bread_experts_group.io.SequentialIOLayout

data class ISO9660Disc(
	val systemArea: ByteArray = ByteArray(32768),
	val volumeDescriptors: MutableList<ISO9660VolumeDescriptor> = mutableListOf()
) {
	companion object {
		val layout = SequentialIOLayout(
			::ISO9660Disc,
			IOLayout.BYTE.sequence(32768).array(),
			PrimitiveIOLayout(
				{ r ->
					buildList {
						while (true) {
							val read = ISO9660VolumeDescriptor.layout.read(r)
							add(read)
							if (read.type.enum == ISO9660VolumeType.TERMINATOR) break
						}
					}
				},
				{ w, i -> TODO("GAMMA") }
			)
		)
	}

	override fun toString(): String = "ISO 9660 Disc" +
			"\n\tSystem Area: ${systemArea.size} bytes" +
			"\n\tDescriptors: [${volumeDescriptors.size}]\n\t\t" +
			volumeDescriptors.joinToString("\n\t\t") {
				it.toString().replace("\n", "\n\t\t")
			}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as ISO9660Disc

		if (!systemArea.contentEquals(other.systemArea)) return false
		if (volumeDescriptors != other.volumeDescriptors) return false

		return true
	}

	override fun hashCode(): Int {
		var result = systemArea.contentHashCode()
		result = 31 * result + volumeDescriptors.hashCode()
		return result
	}
}