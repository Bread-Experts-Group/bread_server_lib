package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.Mappable

enum class EDNS0OptionValue(override val id: UShort, override val tag: String) : Mappable<EDNS0OptionValue, UShort> {
	COOKIE(10u, "COOKIE")
}