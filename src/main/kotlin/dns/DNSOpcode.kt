package bread_experts_group.dns

enum class DNSOpcode(val code: Int) {
	QUERY(0),
	INVERSE_QUERY(1),
	STATUS(2);

	companion object {
		val mapping = entries.associateBy(DNSOpcode::code)
	}
}