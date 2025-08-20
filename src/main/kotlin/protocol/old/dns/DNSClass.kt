package org.bread_experts_group.protocol.old.dns

enum class DNSClass(val code: Int) {
	IN__INTERNET(1),
	CS__CSNET(2),
	CH__CHAOS(3),
	HS__HEIOD(4),
	OTHER(-1);

	companion object {
		val mapping: Map<Int, DNSClass> = entries.associateBy(DNSClass::code)
	}
}