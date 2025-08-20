package org.bread_experts_group.computer.disc.el_torito

import org.bread_experts_group.coder.Mappable

enum class ElToritoPlatform(override val id: UByte, override val tag: String) : Mappable<ElToritoPlatform, UByte> {
	X86(0u, "x86 (80x86)"),
	POWER_PC(1u, "PowerPC"),
	MAC(1u, "Macintosh");

	override fun toString(): String = stringForm()
}