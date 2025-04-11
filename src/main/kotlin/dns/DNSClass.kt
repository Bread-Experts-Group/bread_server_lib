package bread_experts_group.dns

enum class DNSClass(val code: Int) {
	IN__INTERNET(1),
	CS__CSNET(2),
	CH__CHAOS(3),
	HS__HEIOD(4);

	companion object {
		val mapping = entries.associateBy(DNSClass::code)
	}
}