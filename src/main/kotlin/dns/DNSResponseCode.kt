package bread_experts_group.dns

enum class DNSResponseCode(val code: Int) {
	OK(0),
	FORMAT_ERROR(1),
	SERVER_FAILURE(2),
	NONEXISTENT_DOMAIN(3);

	companion object {
		val mapping = entries.associateBy(DNSResponseCode::code)
	}
}