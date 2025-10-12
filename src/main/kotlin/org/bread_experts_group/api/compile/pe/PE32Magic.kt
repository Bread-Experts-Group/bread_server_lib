package org.bread_experts_group.api.compile.pe

import org.bread_experts_group.Mappable

enum class PE32Magic(override val id: UShort, override val tag: String) : Mappable<PE32Magic, UShort> {
	ROM(0x107u, "ROM"),
	PE_32(0x10Bu, "PE32"),
	PE_32_PLUS(0x20Bu, "PE32+")
}