package org.bread_experts_group.dns.opt

enum class DNSOptionType(val code: Int) {
	OTHER(-1);

	companion object {
		val mapping = entries.associateBy(DNSOptionType::code)
	}
}