package org.bread_experts_group.protocol.dns.opt

open class DNSOption(
	val type: DNSOptionType,
	val typeRaw: Int,
	val data: ByteArray
) {
	override fun toString(): String = "(DNS, Option) $type [$typeRaw], # DATA: [$data]"
}