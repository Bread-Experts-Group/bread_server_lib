package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.Mappable

enum class EDNS0ResponseCode(override val id: UInt, override val tag: String) : Mappable<EDNS0ResponseCode, UInt> {
	;

	override fun toString(): String = stringForm()
}