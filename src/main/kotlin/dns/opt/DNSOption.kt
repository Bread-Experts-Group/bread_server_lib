package org.bread_experts_group.dns.opt

open class DNSOption(
	val type: DNSOptionType,
	val typeRaw: Int,
	val data: ByteArray
)