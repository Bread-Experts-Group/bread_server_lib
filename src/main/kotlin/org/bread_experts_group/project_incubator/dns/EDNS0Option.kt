package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.MappedEnumeration

abstract class EDNS0Option(val value: MappedEnumeration<UShort, EDNS0OptionValue>) {
	class Generic(
		value: MappedEnumeration<UShort, EDNS0OptionValue>,
		val data: ByteArray
	) : EDNS0Option(value)

	override fun toString(): String = "EDNS(0) Option $value"
}