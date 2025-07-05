package org.bread_experts_group.protocol.dns.opt

enum class DNSOptionType(val code: Int) {
	OTHER(-1);

	companion object {
		val mapping: Map<Int, DNSOptionType> = entries.associateBy(DNSOptionType::code)
	}
}