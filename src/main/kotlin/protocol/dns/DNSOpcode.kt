package org.bread_experts_group.protocol.dns

enum class DNSOpcode(val code: Int) {
	QUERY(0),
	INVERSE_QUERY(1),
	STATUS(2),
	NOTIFY(4),
	UPDATE(5),
	STATEFUL_OPERATIONS(6),
	OTHER(-1);

	companion object {
		val mapping: Map<Int, DNSOpcode> = entries.associateBy(DNSOpcode::code)
	}
}