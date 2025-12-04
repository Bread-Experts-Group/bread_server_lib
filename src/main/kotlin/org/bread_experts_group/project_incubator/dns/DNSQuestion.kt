package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.MappedEnumeration

data class DNSQuestion(
	val domain: String,
	val questionType: MappedEnumeration<UShort, DNSType>,
	val questionClass: MappedEnumeration<UShort, DNSClass>
) {
	override fun toString(): String = "DNS Question \"$domain\" @ $questionClass ($questionType)"
}