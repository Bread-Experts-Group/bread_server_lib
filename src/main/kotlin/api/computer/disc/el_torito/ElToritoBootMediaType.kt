package org.bread_experts_group.api.computer.disc.el_torito

import org.bread_experts_group.coder.Mappable

enum class ElToritoBootMediaType(
	override val id: UByte,
	override val tag: String
) : Mappable<ElToritoBootMediaType, UByte> {
	NO_EMULATION(0u, "No Emulation"),
	DISKETTE_1_2M(1u, "1.2M Diskette"),
	DISKETTE_1_44M(2u, "1.44M Diskette"),
	DISKETTE_2_88M(3u, "2.88M Diskette"),
	HARD_DISK(4u, "Hard Disk Drive (#80)");

	override fun toString(): String = stringForm()
}