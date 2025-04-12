package bread_experts_group.dns

enum class DNSResponseCode(val code: Int) {
	OK(0),
	FORMAT_ERROR(1),
	SERVER_FAILURE(2),
	NONEXISTENT_DOMAIN(3),
	NOT_IMPLEMENTED(4),
	SERVER_REFUSED(5),
	PARADOXICAL_DOMAIN(6),
	PARADOXICAL_RECORD_SET(7),
	SERVER_NOT_AUTHORITATIVE(8),
	NOT_IN_ZONE(9);

	companion object {
		val mapping = entries.associateBy(DNSResponseCode::code)
	}
}