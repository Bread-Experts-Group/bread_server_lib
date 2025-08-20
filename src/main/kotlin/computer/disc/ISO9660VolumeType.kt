package org.bread_experts_group.computer.disc

import org.bread_experts_group.coder.Mappable

enum class ISO9660VolumeType(override val id: UByte, override val tag: String) : Mappable<ISO9660VolumeType, UByte> {
	BOOT_RECORD(0u, "Boot Record"),
	PRIMARY(1u, "Primary"),
	SUPPLEMENTARY(2u, "Supplementary"),
	TERMINATOR(255u, "Terminator");

	override fun toString(): String = stringForm()
}